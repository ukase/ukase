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

package com.github.ukase.web;

import com.github.ukase.TestHelper;

import com.github.ukase.UkaseApplication;
import com.github.ukase.config.BulkConfig;
import com.github.ukase.config.UkaseSettings;
import com.github.ukase.config.WaterMarkSettings;
import com.github.ukase.config.WebConfig;
import com.github.ukase.async.AsyncManager;
import com.github.ukase.service.HtmlRenderer;
import com.github.ukase.service.PdfRenderer;
import com.github.ukase.service.PdfWatermarkRenderer;
import com.github.ukase.service.XlsxRenderer;
import com.github.ukase.toolkit.CompoundSource;
import com.github.ukase.toolkit.CompoundTemplateLoader;
import com.github.ukase.toolkit.fs.FSTemplateLoader;
import com.github.ukase.toolkit.jar.ZipTemplateLoader;
import com.github.ukase.toolkit.render.RenderTaskBuilder;
import com.github.ukase.toolkit.ResourceProvider;
import com.github.ukase.toolkit.fs.FileSource;
import com.github.ukase.toolkit.helpers.FormatDateHelper;
import com.github.ukase.toolkit.helpers.FormatNumberHelper;
import com.github.ukase.toolkit.jar.JarSource;
import com.github.ukase.toolkit.xlsx.RenderingTableFactory;
import com.github.ukase.toolkit.xlsx.translators.BackgroundColorTranslator;
import com.github.ukase.toolkit.xlsx.translators.BorderBottomTranslator;
import com.github.ukase.toolkit.xlsx.translators.BorderLeftTranslator;
import com.github.ukase.toolkit.xlsx.translators.BorderRightTranslator;
import com.github.ukase.toolkit.xlsx.translators.BorderTopTranslator;
import com.github.ukase.toolkit.xlsx.translators.FontSizeTranslator;
import com.github.ukase.toolkit.xlsx.translators.FontWeightTranslator;
import com.github.ukase.toolkit.xlsx.translators.HorizontalAlignmentTranslator;
import com.github.ukase.toolkit.xlsx.translators.VerticalAlignmentTranslator;
import com.github.ukase.toolkit.xlsx.translators.WordWrapTranslator;
import com.github.ukase.web.validation.HtmlTemplateLocationExistsValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = {
        UkaseApplication.class,
        UkaseSettings.class,
        WebConfig.class,
        WaterMarkSettings.class,
        BulkConfig.class,
        AsyncManager.class,
        HtmlRenderer.class,
        PdfRenderer.class,
        PdfWatermarkRenderer.class,
        XlsxRenderer.class,
        FileSource.class,
        FormatDateHelper.class,
        FormatNumberHelper.class,
        JarSource.class,
        CompoundSource.class,
        CompoundTemplateLoader.class,
        RenderTaskBuilder.class,
        ResourceProvider.class,
        HtmlTemplateLocationExistsValidator.class,
        RenderingTableFactory.class,
        BackgroundColorTranslator.class,
        BorderBottomTranslator.class,
        BorderLeftTranslator.class,
        BorderRightTranslator.class,
        BorderTopTranslator.class,
        FontSizeTranslator.class,
        FontWeightTranslator.class,
        HorizontalAlignmentTranslator.class,
        VerticalAlignmentTranslator.class,
        WordWrapTranslator.class,
        ZipTemplateLoader.class,
        FSTemplateLoader.class,
})
@WebMvcTest(controllers = {UkaseController.class, UkaseExceptionHandler.class})
public class UkaseControllerTest {
    private static final String payload;
    static {
        try {
            payload = TestHelper.getFileContent("basic-payload.json", UkaseControllerTest.class);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read test data");
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGenerateHtml() throws Exception {
        String basicHtml = getFileContent("basic.html");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/html").content(payload).contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(basicHtml));
    }

    @Test
    public void testWrongTemplate() throws Exception {
        String wrongPayload = getFileContent("wrong-template-payload.json");
        String wrongAnswer = getFileContent("wrong-template.answer.json");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/html").content(wrongPayload).contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(wrongAnswer));
    }

    @Test
    public void testBrokenHelper() throws Exception {
        String brokenPayload = getFileContent("broken-payload.json");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/html").content(brokenPayload).contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void testGeneratePdf() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/pdf").content(payload).contentType("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(this::checkPdf);
    }

    private void checkPdf(MvcResult result) {
        if (result.getResponse().getContentLength() <= 0) {
            fail("Generated pdf is empty");
        }
    }

    private String getFileContent(String fileName) throws IOException {
        return TestHelper.getFileContent(fileName, getClass());
    }
}