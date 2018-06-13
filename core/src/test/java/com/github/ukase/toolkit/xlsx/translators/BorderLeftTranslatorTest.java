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
import org.apache.poi.ss.usermodel.BorderStyle;
import org.junit.Test;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.derived.BorderPropertySet;
import org.xhtmlrenderer.newtable.CollapsedBorderValue;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class BorderLeftTranslatorTest extends BorderTranslatorTests {
    private final BorderLeftTranslator translator = new BorderLeftTranslator();

    @Test
    public void testNoBorder() {
        testBottomBorder(0, NONE_BORDER, BorderStyle.NONE);
    }

    @Test
    public void testThinBorder() {
        testBottomBorder(1, NONE_BORDER, BorderStyle.THIN);
    }

    @Test
    public void testHairBorder() {
        testBottomBorder(1, DOTTED, BorderStyle.HAIR);
    }

    @Test
    public void testMediumBorder() {
        testBottomBorder(21, NONE_BORDER, BorderStyle.MEDIUM);
    }

    @Test
    public void testDottedBorder() {
        testBottomBorder(21, DOTTED, BorderStyle.DOTTED);
    }

    @Test
    public void testDashedBorder() {
        testBottomBorder(21, DASHED, BorderStyle.DASHED);
    }

    @Test
    public void testThickBorder() {
        testBottomBorder(41, NONE_BORDER, BorderStyle.THICK);
    }

    @Test
    public void testMediumDashDotBorder() {
        testBottomBorder(41, DOTTED, BorderStyle.MEDIUM_DASH_DOT);
    }

    @Test
    public void testMediumDashedBorder() {
        testBottomBorder(41, DASHED, BorderStyle.MEDIUM_DASHED);
    }


    private void testBottomBorder(float size, CollapsedBorderValue left, BorderStyle expected) {
        BorderPropertySet propertySet = new BorderPropertySet(NONE_BORDER, NONE_BORDER, NONE_BORDER, left);
        propertySet.setLeft(size);

        CalculatedStyle style = mock(CalculatedStyle.class);
        onlyFunction(null, style::getBorder, style, propertySet);

        CellStyleKey key = new CellStyleKey();

        translator.translateCssToXlsx(style, key);
        assertEquals(expected, key.getBorderLeft());
    }
}