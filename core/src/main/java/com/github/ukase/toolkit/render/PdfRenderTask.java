/*
 * Copyright (c) 2018 Pavel Uvarov <pauknone@yahoo.com>
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

package com.github.ukase.toolkit.render;

import com.github.ukase.service.Renderer;
import com.github.ukase.web.UkasePayload;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
class PdfRenderTask implements RenderTask {
    private final UkasePayload payload;
    private final Renderer<UkasePayload, String> htmlRenderer;
    private final Renderer<String, byte[]> pdfRenderer;
    private final Renderer<byte[], byte[]> watermarkRenderer;

    PdfRenderTask(UkasePayload payload,
                  Renderer<UkasePayload, String> htmlRenderer,
                  Renderer<String, byte[]> pdfRenderer,
                  Renderer<byte[], byte[]> watermarkRenderer) {
        this.payload = payload;
        this.htmlRenderer = htmlRenderer;
        this.pdfRenderer = pdfRenderer;
        this.watermarkRenderer = watermarkRenderer;
    }

    @Override
    public byte[] call() throws RenderException {
        try {
            log.debug("Start processing: {}", payload.getIndex());
            String html = htmlRenderer.render(payload);
            log.debug("Prepared xhtml:\n{}\n", html);
            byte[] renderedData = pdfRenderer.render(html);
            log.debug("Processed successfully: {}", payload.getIndex());
            if (payload.isSample()) {
                renderedData = watermarkRenderer.render(renderedData);
                log.debug("Processed sample watermark for: {}", payload.getIndex());
            }
            return renderedData;
        } catch (RenderException e) {
            e.setPayload(payload);
            throw e;
        }
    }

    @Override
    public String getTemplateName() {
        return payload.getIndex();
    }
}
