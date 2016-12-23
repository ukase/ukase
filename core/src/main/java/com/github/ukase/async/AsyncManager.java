/*
 * Copyright (c) 2016 Konstantin Lepa <konstantin+ukase@lepabox.net>
 *
 * This file is part of Ukase.
 *
 *  Ukase is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.ukase.async;

import com.github.ukase.toolkit.render.RenderTask;
import com.github.ukase.toolkit.render.RenderTaskBuilder;
import com.github.ukase.web.UkasePayload;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
@Log4j
public class AsyncManager {
    private static final int SUB_DIR_NAME_LENGTH = 8;

    private final ConcurrentMap<String, AsyncRequest> renderedPDFs = new ConcurrentHashMap<>();

    private ExecutorService executor;
    private RenderTaskBuilder builder;
    private Long ttl;
    private File path;

    @Autowired
    public AsyncManager(ExecutorService executor, RenderTaskBuilder builder, Long ttl, File path) {
        this.executor = executor;
        this.builder = builder;
        this.ttl = ttl;
        this.path = path;
        checkDataDirectory();
    }

    public String putTaskInOrder(List<UkasePayload> payloads) {
        return startProcessingPdf(payloads).getId();
    }

    public String putXlsxTaskInOrder(UkasePayload payload) {
        return startProcessingXlsx(payload).getId();
    }

    public byte[] processOrder(List<UkasePayload> payloads) throws InterruptedException {
        return startProcessingPdf(payloads).getResult();
    }

    public AsyncStatus checkStatus(String id) {
        return renderedPDFs.get(id).getStatus();
    }

    public byte[] getOrder(String id) {
        if (checkStatus(id) == AsyncStatus.PROCESSED) {
            return getFileData(id);
        }
        return null;
    }

    void clearOldPDFs() {
        renderedPDFs.forEach(this::checkTTL);
    }

    private void checkTTL(String id, AsyncRequest request) {
        if (request == null || request.getStatus() == AsyncStatus.ORDERED || request.getBuildTime() == null) {
            return;
        }
        if (System.currentTimeMillis() - request.getBuildTime() > ttl) {
            File pdf = request.getDataFile();
            if (!pdf.delete()) {
                log.warn("PDF for " + id + " weren't deleted");
            }

            renderedPDFs.remove(id);

            File subDir = pdf.getParentFile();
            File[] files = subDir.listFiles();
            if (files != null && files.length == 0) {
                log.info(subDir.getName() +
                        " removing... " +
                        (subDir.delete() ? "success" : "failed") );
            }
        }
    }

    private AsyncTask startProcessingXlsx(UkasePayload payload) {
        RenderTask renderTask = builder.buildXlsx(payload);
        AsyncTask task = new AsyncRenderTask(renderTask, nextRequest());
        return task.startOnExecutor(executor);
    }

    private AsyncTask startProcessingPdf(List<UkasePayload> payloads) {
        List<RenderTask> tasks = payloads.stream()
                .map(builder::build)
                .collect(Collectors.toList());
        AsyncRequest request = nextRequest();

        BulkRenderTask task = new BulkRenderTask(tasks, request);
        return task.startOnExecutor(executor);
    }

    private void checkDataDirectory() {
        File[] directories = path.listFiles();
        if (directories == null) {
            return;
        }
        Arrays.stream(directories).forEach(this::checkSubDirectories);
    }

    private void checkSubDirectories(File subDir) {
        if (subDir.isDirectory() && subDir.getName().length() == SUB_DIR_NAME_LENGTH) {
            File[] files = subDir.listFiles();
            if (files == null) {
                return;
            }
            Arrays.stream(files).forEach(this::registerDataFile);
        }
    }

    private void registerDataFile(File dataFile) {
        String fileName = dataFile.getName();
        if (dataFile.isFile()) {
            String id = fileName;
            if (id.endsWith(".pdf")) {
                id = id.substring(0, id.length() - 4);
            }
            renderedPDFs.computeIfAbsent(id,
                    uuid -> new AsyncRequest(dataFile, uuid, dataFile.lastModified()));
        }
    }

    private byte[] getFileData(String id) {
        File pdf = getDataFile(id);
        Path pdfPath = pdf.getAbsoluteFile().toPath();
        try {
            return Files.readAllBytes(pdfPath);
        } catch (IOException e) {
            log.error("Cannot read pdf file data " + pdf.getAbsolutePath());
            return null;
        }
    }

    private File getSubDir(String id) {
        return new File(path, getSubDirName(id));
    }

    private String getSubDirName(String id) {
        return id.substring(0, SUB_DIR_NAME_LENGTH);
    }

    private File getDataFile(String id) {
        return getDataFile(id, getSubDir(id));
    }

    private File getDataFile(String id, File subDir) {
        return new File(subDir, id);
    }


    private AsyncRequest nextRequest() {
        String uuid = nextUUID();
        AsyncRequest request = new AsyncRequest();
        while (renderedPDFs.putIfAbsent(uuid, request) != null) {
            uuid = nextUUID();
        }
        request.setUuid(uuid);
        request.setDataFile(getDataFile(request.getUuid()));
        return request;
    }
    private String nextUUID() {
        return UUID.randomUUID().toString();
    }
}
