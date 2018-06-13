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
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.junit.Test;
import org.mockito.Mockito;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.FSDerivedValue;

import static org.junit.Assert.*;

public class VerticalAlignmentTranslatorTest extends MockedTests {
    private final VerticalAlignmentTranslator translator = new VerticalAlignmentTranslator();

    @Test
    public void testNoAlignment() {
        testAlignment(null, VerticalAlignment.BOTTOM);
    }

    @Test
    public void testWrongAlignment() {
        testAlignment(IdentValue.ALWAYS, VerticalAlignment.BOTTOM);
    }

    @Test
    public void testBottomAlignment() {
        testAlignment(IdentValue.BOTTOM, VerticalAlignment.BOTTOM);
    }

    @Test
    public void testTopAlignment() {
        testAlignment(IdentValue.TOP, VerticalAlignment.TOP);
    }

    @Test
    public void testCenterAlignment() {
        testAlignment(IdentValue.MIDDLE, VerticalAlignment.CENTER);
    }

    private void testAlignment(FSDerivedValue value, VerticalAlignment expected) {
        CalculatedStyle style = Mockito.mock(CalculatedStyle.class);
        onlyFunction(CSSName.VERTICAL_ALIGN, style::valueByName, style, value);

        CellStyleKey key = new CellStyleKey();
        translator.translateCssToXlsx(style, key);

        assertEquals(expected, key.getVerticalAlignment());
    }
}