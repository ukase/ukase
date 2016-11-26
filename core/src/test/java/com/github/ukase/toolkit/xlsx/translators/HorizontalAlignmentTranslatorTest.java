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

package com.github.ukase.toolkit.xlsx.translators;

import com.github.ukase.toolkit.xlsx.CellStyleKey;
import com.github.ukase.toolkit.xlsx.MockedTests;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.junit.Test;
import org.mockito.Mockito;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.style.CalculatedStyle;

import static org.junit.Assert.*;

public class HorizontalAlignmentTranslatorTest extends MockedTests {
    private final HorizontalAlignmentTranslator translator = new HorizontalAlignmentTranslator();

    @Test
    public void testNoAlignment() {
        testAlignment(IdentValue.NONE, HorizontalAlignment.CENTER);
    }

    @Test
    public void testCenterAlignment() {
        testAlignment(IdentValue.CENTER, HorizontalAlignment.CENTER);
    }

    @Test
    public void testRightAlignment() {
        testAlignment(IdentValue.RIGHT, HorizontalAlignment.RIGHT);
    }

    @Test
    public void testLeftAlignment() {
        testAlignment(IdentValue.LEFT, HorizontalAlignment.LEFT);
    }

    @Test
    public void testJustifyAlignment() {
        testAlignment(IdentValue.JUSTIFY, HorizontalAlignment.JUSTIFY);
    }


    private void testAlignment(IdentValue value, HorizontalAlignment expected) {
        CalculatedStyle style = Mockito.mock(CalculatedStyle.class);
        onlyFunction(CSSName.TEXT_ALIGN, style::getIdent, style, value);

        CellStyleKey key = new CellStyleKey();
        translator.translateCssToXlsx(style, key);

        assertEquals(expected, key.getHorizontalAlignment());

    }
}