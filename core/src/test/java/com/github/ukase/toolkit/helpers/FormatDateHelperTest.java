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

package com.github.ukase.toolkit.helpers;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.TagType;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class FormatDateHelperTest {
    private static final FormatDateHelper HELPER = new FormatDateHelper();
    private static final String REASON_WRONG = "Wrong render";
    private static final Long LONG_DATE = 12312612341234L;
    private static final String STRING_DATE_TIME = "04.03.2360 05:05";
    private static final String STRING_DATE = "04.03.2360";

    @Test
    public void testNull() throws Exception {
        assertEquals(REASON_WRONG, "", HELPER.apply(null, null));
    }

    @Test
    public void testLong() throws Exception {
        test(LONG_DATE, FormatDateHelper.DATE_FORMAT, STRING_DATE);
    }

    @Test
    public void testStringDateTime() throws Exception {
        test(STRING_DATE_TIME, FormatDateHelper.DATE_FORMAT, STRING_DATE);
    }

    @Test
    public void testStringDate() throws Exception {
        test(STRING_DATE, FormatDateHelper.DATE_FORMAT, STRING_DATE);
    }

    @Test
    public void testWrongString() throws Exception {
        String context = "dg213f43f";
        Options options = getOptions(context, FormatDateHelper.DATE_FORMAT);
        assertEquals(REASON_WRONG, "", HELPER.apply(context, options));
    }

    @Test
    public void testStringCustomFormat() throws Exception {
        String parseFormat = "yyyy.[MM].dd в HH:mm";//
        String context = "2360.[03].04 в 05:05";
        Options options = getOptions(context, FormatDateHelper.DATE_FORMAT);
        options.hash.put("parseFormat", parseFormat);
        assertEquals(REASON_WRONG, STRING_DATE, HELPER.apply(context, options));
    }

    private void test(Object context, String format, String result) throws Exception {
        Options options = getOptions(context, format);
        assertEquals(REASON_WRONG, result, HELPER.apply(context, options));
    }

    private Options getOptions(Object context, String... params) {
        return new Options(null,
                "format_date",
                TagType.VAR,
                Context.newContext(context),
                null,
                null,
                params,
                new HashMap<>(),
                null);
    }
}