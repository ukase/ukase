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

package com.github.ukase.toolkit.render;

import com.github.ukase.service.HtmlRenderer;
import com.github.ukase.service.PdfRenderer;
import com.github.ukase.service.PdfWatermarkRenderer;
import com.github.ukase.service.XlsxRenderer;
import com.github.ukase.web.UkasePayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RenderTaskBuilder {
    private HtmlRenderer htmlRenderer;
    private PdfRenderer pdfRenderer;
    private PdfWatermarkRenderer watermarkRenderer;
    private XlsxRenderer xlsxRenderer;

    @Autowired
    public RenderTaskBuilder(HtmlRenderer htmlRenderer,
                             PdfRenderer pdfRenderer,
                             PdfWatermarkRenderer watermarkRenderer,
                             XlsxRenderer xlsxRenderer) {
        this.htmlRenderer = htmlRenderer;
        this.pdfRenderer = pdfRenderer;
        this.watermarkRenderer = watermarkRenderer;
        this.xlsxRenderer = xlsxRenderer;
    }

    public RenderTask build(UkasePayload payload) {
        return new PdfRenderTask(payload, htmlRenderer, pdfRenderer, watermarkRenderer);
    }

    public RenderTask buildXlsx(UkasePayload payload) {
        return new XlsxRendererTask(payload, htmlRenderer, xlsxRenderer);
    }

}
