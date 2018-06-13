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

package com.github.ukase.service;

import com.github.ukase.config.WaterMarkSettings;
import com.github.ukase.toolkit.render.RenderException;
import com.github.ukase.toolkit.ResourceProvider;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfWatermarkRenderer implements Renderer<byte[], byte[]> {
    private final WaterMarkSettings waterMark;
    private final Font font;

    @Autowired
    public PdfWatermarkRenderer(WaterMarkSettings waterMark, ResourceProvider provider)
            throws IOException, DocumentException {
        this.waterMark = waterMark;
        BaseFont baseFont = BaseFont.createFont(provider.getDefaultFont(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        this.font = new Font(baseFont, waterMark.getSize(), 0, BaseColor.LIGHT_GRAY);
    }

    @Override
    public byte[] render(byte[] data) throws RenderException {
        try {
            PdfReader reader = new PdfReader(data);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Phrase phrase = new Phrase(waterMark.getText(), font);
            PdfStamper stamper = new PdfStamper(reader, baos);
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                PdfContentByte canvas = stamper.getUnderContent(i);
                ColumnText.showTextAligned(canvas,
                        Element.ALIGN_CENTER,
                        phrase, waterMark.getX(),
                        waterMark.getY(),
                        waterMark.getDegree());
            }
            stamper.close();
            reader.close();
            return baos.toByteArray();
        } catch (IOException|DocumentException e) {
            throw new RenderException("Cannot add watermark" , e, "watermark");
        }
    }
}
