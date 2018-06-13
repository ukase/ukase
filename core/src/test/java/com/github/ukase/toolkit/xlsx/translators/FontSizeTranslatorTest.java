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

package com.github.ukase.toolkit.xlsx.translators;

import com.github.ukase.toolkit.xlsx.CellStyleKey;
import com.github.ukase.toolkit.xlsx.MockedTests;
import org.junit.Test;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.FSDerivedValue;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class FontSizeTranslatorTest extends MockedTests {
    private final FontSizeTranslator translator = new FontSizeTranslator();

    @Test
    public void testNoSize() {
        testNull(null);
    }

    @Test
    public void testWrongSize() {
        testNull(stringValue("11px", CSSName.FONT_SIZE));
    }

    @Test
    public void test11pt() {
        testPt((short)11);
    }

    @Test
    public void test14pt() {
        testPt((short)14);
    }

    private void testPt(short size) {
        CalculatedStyle style = mock(CalculatedStyle.class);
        FSDerivedValue value = stringValue(size + "pt", CSSName.FONT_SIZE);
        onlyFunction(CSSName.FONT_SIZE, style::valueByName, style, value);

        CellStyleKey key = new CellStyleKey();

        translator.translateCssToXlsx(style, key);
        assertEquals("Wrong translation", (long)key.getFontSize(), (long)size);
    }

    private void testNull(FSDerivedValue value) {
        CalculatedStyle style = mock(CalculatedStyle.class);
        onlyFunction(CSSName.FONT_SIZE, style::valueByName, style, value);

        CellStyleKey key = new CellStyleKey();

        translator.translateCssToXlsx(style, key);
        assertNull(key.getFontSize());
    }
}