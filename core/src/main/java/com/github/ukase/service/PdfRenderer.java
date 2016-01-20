/*
 * Copyright (c) 2015 Konstantin Lepa <konstantin+ukase@lepabox.net>
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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.github.ukase.toolkit.ResourceProvider;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.github.ukase.config.UkaseSettings;
import org.xhtmlrenderer.util.XRRuntimeException;
import org.xml.sax.SAXParseException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

@Service
@Log4j
public class PdfRenderer {
    private static final String COMMON_DOCTYPE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" " +
            "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
    private static final String DOCTYPE_HTML5 = "<!DOCTYPE html>";
    private static final Pattern HTML_TAG = Pattern.compile("<html([^>]*)lang=\"([^\"]+)\"([^>]*)>");
    private static final String HTML_TAG_REPLACEMENT =
            "<html$1lang=\"$2\" xml:lang=\"$2\" xmlns=\"http://www.w3.org/1999/xhtml\"$3>";

    private final String resourcesPath;
    private final ResourceProvider provider;
    private final WaterMarkSettings waterMark;

    @Autowired
    private PdfRenderer(UkaseSettings settings, ResourceProvider provider) {
        File resources = settings.getResources();
        if (resources == null || !resources.isDirectory()) {
            resourcesPath = null;
        } else {
            resourcesPath = resources.toURI().toString();
        }

        this.provider = provider;
        this.waterMark = settings.getWaterMark();
    }

    public byte[] render(String html, boolean sample) throws DocumentException, IOException, URISyntaxException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ITextRenderer renderer = provider.getRenderer();
        try {
            if (resourcesPath != null) {
                renderer.setDocumentFromString(wrapHtml5Document(html), resourcesPath);
            } else {
                renderer.setDocumentFromString(wrapHtml5Document(html));
            }
        } catch (XRRuntimeException e) {
            if (e.getCause() instanceof SAXParseException) {
                SAXParseException parseException = (SAXParseException) e.getCause();
                String[] lines = html.split("\r\n|\r|\n");
                log.warn("Error in line -->\n" + lines[parseException.getLineNumber() - 1] + "\n<--", e);
            }
        }
        renderer.layout();
        char pdfVersion = renderer.getPDFVersion();
        renderer.createPDF(baos, true);
        renderer.finishPDF();

        if (sample) {
            addSampleWatermark(baos, pdfVersion);
        }

        return baos.toByteArray();
    }

    private void addSampleWatermark(ByteArrayOutputStream baos, char pdfVersion) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(baos.toByteArray());
        baos.reset();
        Font font = new Font(Font.FontFamily.UNDEFINED, waterMark.getSize(), 0, BaseColor.LIGHT_GRAY);
        Phrase phrase = new Phrase(waterMark.getText(), font);
        PdfStamper stamper = new PdfStamper(reader, baos, pdfVersion);
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
    }

    private String wrapHtml5Document(String html5PossibleDocument) {
        String document = html5PossibleDocument;

        if (html5PossibleDocument.contains(DOCTYPE_HTML5)) {
            document = document.replace(DOCTYPE_HTML5, COMMON_DOCTYPE);
            document = HTML_TAG.matcher(document).replaceAll(HTML_TAG_REPLACEMENT);
        }

        return filterKnownEntities(document);
    }

    private String filterKnownEntities(String html) {
        return html.replace("&#x27;", "'")
                .replace("&#x3D;", "=")
                .replace("&#x3D", "=");
    }
}
