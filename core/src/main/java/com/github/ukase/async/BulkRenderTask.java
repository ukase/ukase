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

import com.github.ukase.toolkit.render.RenderException;
import com.github.ukase.toolkit.render.RenderTask;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.log4j.Log4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Log4j
class BulkRenderTask implements AsyncTask {
    private final List<SingleRenderTask> subTasks;
    private List<Future<byte[]>> taskOrder;
    private final CountDownLatch latch;
    private final AsyncRequest request;
    private byte[] data;

    BulkRenderTask(List<RenderTask> payloads, AsyncRequest request) {
        this.request = request;
        subTasks = payloads.stream().map(this::createSubTask).collect(Collectors.toList());
        latch = new CountDownLatch(subTasks.size() + 1);
    }

    @Override
    public String getId() {
        return request.getUuid();
    }

    @Override
    public synchronized BulkRenderTask startOnExecutor(ExecutorService service) {
        if (taskOrder != null) {
            return this;
        }
        try {
            taskOrder = service.invokeAll(subTasks);
            if (subTasks.size() == 0) {
                latch.countDown();
            }
        } catch (InterruptedException e) {
            reject();
        }
        return this;
    }

    @Override
    public byte[] getResult() throws InterruptedException {
        latch.await();
        if (data == null) {
            throw new RenderException("Bulk were failed to be rendered", "build bulk pdf");
        }
        return data;
    }

    private void childResult(Boolean renderResult) {
        if (!renderResult) {
            reject();
            return;
        }
        latch.countDown();
        if (latch.getCount() == 1) {
            mergeSubTasks();
        }
    }

    private synchronized void reject() {
        if (taskOrder != null) {
            taskOrder.forEach(future -> future.cancel(true));
        }
        request.error();
    }

    private void mergeSubTasks() {
        try (ByteArrayOutputStream bAOS = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, bAOS);
            document.open();
            PdfContentByte cb = writer.getDirectContent();

            appendPdf(document, writer, cb);

            document.close();
            data = bAOS.toByteArray();
        } catch (IOException | DocumentException e) {
            log.warn("Cannot bulk PDFs", e);
        } finally {
            registerResult();
        }
    }

    private void registerResult() {
        if (data != null) {
            request.saveData(data);
        } else {
            request.error();
        }
        latch.countDown();
    }

    private void appendPdf(Document document, PdfWriter writer, PdfContentByte cb) throws IOException {
        for (SingleRenderTask task : subTasks) {
            if (task.getData() == null) {
                throw new IOException("Some of pdf were failed: " + task.getTemplateName());
            }
            appendPdf(document, writer, cb, task.getData());
        }
    }

    private void appendPdf(Document document, PdfWriter writer, PdfContentByte cb, byte[] pdf) throws IOException {
        PdfReader reader = new PdfReader(pdf);
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            PdfImportedPage page = writer.getImportedPage(reader, i);
            document.newPage();
            cb.addTemplate(page, 0, 0);
        }
    }

    private SingleRenderTask createSubTask(RenderTask task) {
        return new SingleRenderTask(task, this::childResult);
    }
}
