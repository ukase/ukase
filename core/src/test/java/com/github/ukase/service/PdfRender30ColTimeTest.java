/*
 * Copyright (c) 2017 Konstantin Lepa <konstantin+ukase@lepabox.net>
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

import com.github.ukase.web.UkasePayload;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("For manual start only")
public class PdfRender30ColTimeTest extends AbstractNoSpringBenchmark {
    private final HtmlRenderer htmlRenderer;
    private final PdfRenderer pdfRenderer;

    public PdfRender30ColTimeTest() {
        this.htmlRenderer = new HtmlRenderer(getResourceProvider().getEngine());
        this.pdfRenderer = new PdfRenderer(getResourceProvider());
    }

    @Test
    public void pdf05k() {
        test(5_000);
    }

    @Test
    public void pdf10k() {
        test(10_000);
    }

    @Test
    public void pdf20k() {
        test(20_000);
    }

    @Test
    public void pdf30k() {
        test(30_000);
    }

    private void test(int count) {
        UkasePayload data = new UkasePayload();
        data.setData(prepareData(count, 30));
        data.setIndex("templates/31col-performance.pdf");

        System.out.println("Start rendering " + count + ":");
        long start = System.currentTimeMillis();
        String html = htmlRenderer.render(data);
        System.out.println("html " + count + ", ms: " + (System.currentTimeMillis() - start));
        System.out.println(pdfRenderer.render(html).length);
        System.out.println("total, ms: " + (System.currentTimeMillis() - start));
    }
}
