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

package com.github.ukase.bulk;

import com.github.ukase.toolkit.RenderTask;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Log4j
public class BulkRenderTask {
    private final String id;
    private final List<SingleRenderTask> subTasks;
    private final CountDownLatch latch;
    private final Consumer<String> errorConsumer;
    private final BiConsumer<String, byte[]> dataConsumer;
    private byte[] pdf;

    public BulkRenderTask(List<RenderTask> payloads,
                          BiConsumer<String, byte[]> dataConsumer,
                          Consumer<String> errorConsumer) {
        this.dataConsumer = dataConsumer;
        this.errorConsumer = errorConsumer;
        this.id = UUID.randomUUID().toString();
        subTasks = payloads.stream().map(this::createSubTask).collect(Collectors.toList());
        latch = new CountDownLatch(subTasks.size() + 1);
    }

    public String getId() {
        return id;
    }

    public List<Runnable> getSubTasks() {
        return Collections.unmodifiableList(subTasks);
    }

    public byte[] getResult() throws InterruptedException, IOException {
        latch.await();
        if (pdf == null) {
            throw new IOException("Bulk were failed to be rendered");
        }
        return pdf;
    }

    void childProcessed() {
        latch.countDown();
        if (latch.getCount() == 1) {
            mergeSubTasksPdfs();
        }
    }

    private void mergeSubTasksPdfs() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();
            PdfContentByte cb = writer.getDirectContent();

            appendPdfs(document, writer, cb);

            document.close();
            pdf = baos.toByteArray();
        } catch (IOException | DocumentException e) {
            log.warn("Cannot bulk pdfs", e);
        } finally {
            registerResult();
        }
    }

    private void registerResult() {
        if (pdf != null) {
            dataConsumer.accept(id, pdf);
        } else {
            errorConsumer.accept(id);
        }
        latch.countDown();
    }

    private void appendPdfs(Document document, PdfWriter writer, PdfContentByte cb) throws IOException {
        for (SingleRenderTask task: subTasks) {
            if (task.getPdf() == null) {
                throw new IOException("Some of pdf were failed: " + task.getTemplateName());
            }
            appendPdf(document, writer, cb, task.getPdf());
        }
    }

    private void appendPdf(Document document, PdfWriter writer, PdfContentByte cb, byte[] pdf) throws IOException {
        PdfReader reader = new PdfReader(pdf);
        for (int i = 1 ; i <= reader.getNumberOfPages() ; i++) {
            PdfImportedPage page = writer.getImportedPage(reader, i);
            document.newPage();
            cb.addTemplate(page, 0, 0);
        }
    }

    private SingleRenderTask createSubTask(RenderTask task) {
        return new SingleRenderTask(task, this);
    }
}
