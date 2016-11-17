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

import com.github.ukase.config.UkaseSettings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest()
public class PdfRendererTest {
    @Autowired
    private PdfRenderer renderer;
    @Autowired
    private UkaseSettings settings;

    //TODO:
    // check if there are any method to compare pdf files without their metadata (like creation date)
    // or another way to check results of pdf generation.

    @Test
    public void testParseHtml() throws Exception {
        byte[] pdf = getFile("basic.pdf");
        String html = getHtmlData("basic.html");
        assertNotNull(html);
        assertNotNull(pdf);
        savePdf(renderer.render(html, false), "render");
    }

    @Test
    public void testParseSampleHtml() throws Exception {
        byte[] pdf = getFile("basic.pdf");
        String html = getHtmlData("basic.html");
        assertNotNull(html);
        assertNotNull(pdf);
        savePdf(renderer.render(html, true), "render");
    }

    private String getHtmlData(String fileName) throws IOException {
        byte[] data = getFile(fileName);
        if (data == null) {
            return null;
        }
        return new String(data);
    }

    private byte[] getFile(String file) throws IOException {
        File dataFile = new File(settings.getResources(), file);
        if (!dataFile.exists()) {
            return null;
        }
        return Files.readAllBytes(dataFile.toPath());
    }

    private void savePdf(byte[] data, String name) throws IOException {
        Files.write(new File(settings.getResources(), name + ".pdf").toPath(), data);
    }
}