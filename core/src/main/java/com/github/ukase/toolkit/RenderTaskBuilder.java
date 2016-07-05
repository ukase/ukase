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

package com.github.ukase.toolkit;

import com.github.ukase.service.HtmlRenderer;
import com.github.ukase.service.PdfRenderer;
import com.github.ukase.web.UkasePayload;
import com.itextpdf.text.DocumentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;

@Component
@Slf4j
public class RenderTaskBuilder {
    @Autowired
    private HtmlRenderer htmlRenderer;
    @Autowired
    private PdfRenderer pdfRenderer;

    public RenderTask build(UkasePayload payload) {
        return new DefaultRenderTask(payload, htmlRenderer, pdfRenderer);
    }

    private static class DefaultRenderTask implements RenderTask {
        private final UkasePayload payload;
        private final HtmlRenderer htmlRenderer;
        private final PdfRenderer pdfRenderer;

        DefaultRenderTask(UkasePayload payload, HtmlRenderer htmlRenderer, PdfRenderer pdfRenderer) {
            this.payload = payload;
            this.htmlRenderer = htmlRenderer;
            this.pdfRenderer = pdfRenderer;
        }

        @Override
        public byte[] call() throws IOException, DocumentException, URISyntaxException {
            log.debug("Start processing: {}", payload.getIndex());
            String html = htmlRenderer.render(payload.getIndex(), payload.getData());
            log.debug("Prepared xhtml:\n{}\n", html);
            byte[] renderedData = pdfRenderer.render(html, payload.isSample());
            log.debug("Processed successfully: {}", payload.getIndex());
            return renderedData;
        }

        @Override
        public String getTemplateName() {
            return payload.getIndex();
        }
    }
}
