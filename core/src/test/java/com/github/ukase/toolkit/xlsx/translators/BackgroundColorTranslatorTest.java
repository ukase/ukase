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
import org.mockito.Mockito;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.parser.FSCMYKColor;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.derived.ColorValue;

import static org.junit.Assert.*;

public class BackgroundColorTranslatorTest extends MockedTests{
    private final BackgroundColorTranslator translator = new BackgroundColorTranslator();

    @Test
    public void testNoColor() {
        testNullColor(null);
    }

    @Test
    public void testFSCMYKColor() {
        FSColor cmyk = new FSCMYKColor(0, 0, 0, 0);
        testNullColor(cmyk);
    }

    private void testNullColor(FSColor color) {
        ColorValue value = color == null ? null : getColor(color);
        CalculatedStyle style = Mockito.mock(CalculatedStyle.class);
        onlyFunction(CSSName.BACKGROUND_COLOR, style::valueByName, style, value);

        CellStyleKey key = new CellStyleKey();
        translator.translateCssToXlsx(style, key);

        assertNull(key.getBackgroundColor());
    }

    @Test
    public void testRedColor() {
        FSColor red = new FSRGBColor(255, 0, 0);
        testColor(red);
    }

    @Test
    public void testBlueColor() {
        FSColor red = new FSRGBColor(0, 0, 255);
        testColor(red);
    }

    private void testColor(FSColor expected) {
        CalculatedStyle style = Mockito.mock(CalculatedStyle.class);
        onlyFunction(CSSName.BACKGROUND_COLOR, style::valueByName, style, getColor(expected));

        CellStyleKey key = new CellStyleKey();
        translator.translateCssToXlsx(style, key);

        assertEquals(expected, key.getBackgroundColor());
    }

    private ColorValue getColor(FSColor color) {
        PropertyValue propertyValue = new PropertyValue(color);
        return new ColorValue(CSSName.BACKGROUND_COLOR, propertyValue);
    }
}