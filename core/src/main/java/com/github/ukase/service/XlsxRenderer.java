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

import com.github.ukase.toolkit.ResourceProvider;
import com.github.ukase.toolkit.xlsx.ElementList;
import com.github.ukase.toolkit.xlsx.RenderingTable;
import com.github.ukase.toolkit.xlsx.RenderingTableBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.render.BlockBox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Log4j
public class XlsxRenderer {
    private static final String TAG_TABLE = "table";

    private final ResourceProvider provider;

    @Autowired
    public XlsxRenderer(ResourceProvider provider) {
        this.provider = provider;
    }

    public byte[] render(String html) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SXSSFWorkbook wb = new SXSSFWorkbook(-1);

        ITextRenderer renderer = provider.getRenderer(html);
        Document document = renderer.getDocument();
        BlockBox box = renderer.getRootBox();
        RenderingTableBuilder builder = new RenderingTableBuilder(wb, box);

        new ElementList(document.getElementsByTagName(TAG_TABLE)).stream()
                .map(builder::build)
                .forEach(RenderingTable::run);

        wb.write(baos);
        wb.dispose();

        return baos.toByteArray();
    }
}
