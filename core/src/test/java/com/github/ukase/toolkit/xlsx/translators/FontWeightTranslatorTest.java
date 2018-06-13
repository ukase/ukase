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

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class FontWeightTranslatorTest extends MockedTests {
    private final FontWeightTranslator translator = new FontWeightTranslator();

    @Test
    public void testNoWeight() {
        testNull(null);
    }

    @Test
    public void testNotBold() {
        FSDerivedValue value = stringValue("normal", CSSName.FONT_SIZE);
        CalculatedStyle style = mock(CalculatedStyle.class);
        onlyFunction(CSSName.FONT_WEIGHT, style::valueByName, style, value);

        CellStyleKey key = new CellStyleKey();

        translator.translateCssToXlsx(style, key);
        assertFalse(key.getBold());
    }

    @Test
    public void testBold() {
        FSDerivedValue value = stringValue("bold", CSSName.FONT_SIZE);
        CalculatedStyle style = mock(CalculatedStyle.class);
        onlyFunction(CSSName.FONT_WEIGHT, style::valueByName, style, value);

        CellStyleKey key = new CellStyleKey();

        translator.translateCssToXlsx(style, key);
        assertTrue(key.getBold());
    }

    private void testNull(FSDerivedValue value) {
        CalculatedStyle style = mock(CalculatedStyle.class);
        onlyFunction(CSSName.FONT_WEIGHT, style::valueByName, style, value);

        CellStyleKey key = new CellStyleKey();

        translator.translateCssToXlsx(style, key);
        assertNull(key.getBold());
    }
}