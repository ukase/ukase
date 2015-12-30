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

package com.github.ukase.web;

import com.github.ukase.UkaseApplication;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@SpringApplicationConfiguration(UkaseApplication.class)
@WebIntegrationTest(randomPort = true)
public class UkaseControllerTest {
    private static final String payload;
    static {
        try {
            payload = getFileContent("basic-payload.json");
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read test data");
        }
    }

    private static String getFileContent(String fileName) throws IOException {
        InputStream stream = UkaseControllerTest.class.getClassLoader().getResourceAsStream(fileName);
        return StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
    }

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext ctx;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }

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
}