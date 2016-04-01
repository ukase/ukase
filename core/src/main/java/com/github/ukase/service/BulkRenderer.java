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

package com.github.ukase.service;

import com.github.ukase.bulk.BulkRenderTask;
import com.github.ukase.bulk.BulkStatus;
import com.github.ukase.toolkit.RenderTask;
import com.github.ukase.toolkit.RenderTaskBuilder;
import com.github.ukase.web.UkasePayload;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
@Log4j
public class BulkRenderer {
    private static final String PDF_EXT = ".pdf";
    private static final int SUB_DIR_NAME_LENGTH = 8;

    private final ConcurrentMap<String, Long> renderedPDFs = new ConcurrentHashMap<>();

    @Autowired
    private ExecutorService executor;
    @Autowired
    private RenderTaskBuilder builder;
    @Autowired
    private File path;
    @Autowired
    private Long ttl;

    public String putTaskInOrder(List<UkasePayload> payloads) throws IOException {
        return startProcessing(payloads).getId();
    }

    public byte[] processOrder(List<UkasePayload> payloads) throws IOException, InterruptedException {
        return startProcessing(payloads).getResult();
    }

    public BulkStatus checkStatus(String id) {
        Long rendered = renderedPDFs.get(id);
        if (rendered == null) {
            return BulkStatus.ERROR;
        } else if (rendered.equals(Long.MAX_VALUE)) {
            return BulkStatus.ORDERED;
        }
        return BulkStatus.PROCESSED;
    }

    public byte[] getOrder(String id) {
        if (checkStatus(id) == BulkStatus.PROCESSED) {
            getFileData(id);
        }
        return null;
    }

    public void clearOldPDFs() {
        renderedPDFs.forEach(this::checkTTL);
    }

    public void setPath(File path) {
        this.path = path;
        checkDataDirectory();
    }

    private void checkTTL(String id, Long created) {
        if (created == null || created == Long.MAX_VALUE) {
            return;
        }
        if (System.currentTimeMillis() - created > ttl) {
            File pdf = getPdfFile(id);
            if (!pdf.delete()) {
                log.warn("PDF for " + id + " weren't deleted");
            }
            renderedPDFs.remove(id);
        }
    }

    private BulkRenderTask startProcessing(List<UkasePayload> payloads) throws IOException {
        List<RenderTask> tasks = payloads.stream().map(builder::build).collect(Collectors.toList());
        BulkRenderTask task = new BulkRenderTask(tasks, this::registerProcessedData, this::registerFail);
        task.getSubTasks().forEach(executor::execute);
        renderedPDFs.put(task.getId(), Long.MAX_VALUE);

        return task;
    }

    private void registerProcessedData(String id, byte[] data) {
        File subDir = getSubDir(id);
        if (!subDir.isDirectory()) {
            if (!subDir.mkdir()) {
                log.warn("Something wrong with creating pdf's subdir: " + subDir.getAbsolutePath());
            }
        }
        File pdfFile = getPdfFile(id, subDir);
        try (FileOutputStream fos = new FileOutputStream(pdfFile, false)) {
            fos.write(data);
            fos.flush();
        } catch (IOException e) {
            log.error("Cannot create pdf file " + pdfFile.getName(), e);
            registerFail(id);
        }
    }

    private void registerFail(String id) {
        renderedPDFs.remove(id);
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
            File[] pdfs = path.listFiles();
            if (pdfs == null) {
                return;
            }
            Arrays.stream(pdfs).forEach(this::registerPdf);
        }
    }

    private void registerPdf(File pdf) {
        String fileName = pdf.getName();
        if (pdf.isFile() && fileName.endsWith(PDF_EXT)) {
            String id = fileName.substring(0, fileName.length() - 4);
            renderedPDFs.put(id, pdf.lastModified());
        }
    }

    private byte[] getFileData(String id) {
        File pdf = getPdfFile(id);
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

    private File getPdfFile(String id) {
        return getPdfFile(id, getSubDir(id));
    }

    private File getPdfFile(String id, File subDir) {
        return new File(subDir, id + PDF_EXT);
    }
}
