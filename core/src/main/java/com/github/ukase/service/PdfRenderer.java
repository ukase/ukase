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

import com.itextpdf.text.DocumentException;
import com.github.ukase.toolkit.ResourceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.github.ukase.config.UkaseSettings;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

@Service
public class PdfRenderer {
    private static final String COMMON_DOCTYPE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" " +
            "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
    private static final String DOCTYPE_HTML5 = "<!DOCTYPE html>";
    private static final Pattern HTML_TAG = Pattern.compile("<html([^>]*)lang=\"([^\"]+)\"([^>]*)>");
    private static final String HTML_TAG_REPLACEMENT =
            "<html$1lang=\"$2\" xml:lang=\"$2\" xmlns=\"http://www.w3.org/1999/xhtml\"$3>";

    private final String resourcesPath;
    private final ResourceProvider provider;

    @Autowired
    private PdfRenderer(UkaseSettings settings, ResourceProvider provider) {
        File resources = settings.getResources();
        if (resources == null || !resources.isDirectory()) {
            resourcesPath = null;
        } else {
            resourcesPath = resources.toURI().toString();
        }

        this.provider = provider;
    }

    public byte[] render(String html) throws DocumentException, IOException, URISyntaxException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ITextRenderer renderer = provider.getRenderer();
        if (resourcesPath != null) {
            renderer.setDocumentFromString(wrapHtml5Document(html), resourcesPath);
        } else {
            renderer.setDocumentFromString(wrapHtml5Document(html));
        }
        renderer.layout();
        renderer.createPDF(baos, true);
        renderer.finishPDF();

        return baos.toByteArray();
    }

    private String wrapHtml5Document(String html5PossibleDocument) {
        String document = html5PossibleDocument;

        if (html5PossibleDocument.contains(DOCTYPE_HTML5)) {
            document = document.replace(DOCTYPE_HTML5 , COMMON_DOCTYPE);
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
