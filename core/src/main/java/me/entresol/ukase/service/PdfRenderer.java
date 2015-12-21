/*
 * Copyright (c) 2015 Konstantin Lepa <konstantin+ukase@lepabox.net>
 *
 * This file is part of Ukase.
 *
 * Ukase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package me.entresol.ukase.service;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import me.entresol.ukase.config.UkaseSettings;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

@Service
public class PdfRenderer {
    private final String resourcesPath;
    private final File[] fonts;

    @Autowired
    private PdfRenderer(UkaseSettings settings) {
        File resources = new File(settings.getResources());
        if (!resources.isDirectory()) {
            throw new IllegalArgumentException("Wrong configuration - resource path is not a directory");
        }

        resourcesPath = resources.toURI().toString();
        fonts = resources.listFiles((dir, name) -> isFontFileName(name));
    }

    private boolean isFontFileName(String fileName) {
        fileName = fileName.toLowerCase();
        return fileName.endsWith(".eot") || fileName.endsWith(".ttf");
    }

    public byte[] render(String html) throws DocumentException, IOException, URISyntaxException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ITextRenderer renderer = setupFonts(new ITextRenderer());
        renderer.setDocumentFromString(html, resourcesPath);
        renderer.layout();
        renderer.createPDF(baos, true);
        renderer.finishPDF();

        return baos.toByteArray();
    }

    private ITextRenderer setupFonts(ITextRenderer renderer) throws DocumentException, IOException {
        ITextFontResolver resolver = renderer.getFontResolver();
        for (File font: fonts) {
            resolver.addFont(font.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        }
        return renderer;
    }
}
