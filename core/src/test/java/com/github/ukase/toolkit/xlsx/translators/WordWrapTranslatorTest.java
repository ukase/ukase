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
import org.junit.Test;
import org.mockito.Mockito;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.style.CalculatedStyle;

import static org.junit.Assert.*;

public class WordWrapTranslatorTest extends MockedTests {
    private final WordWrapTranslator translator = new WordWrapTranslator();

    @Test
    public void testNoWrap() {
        CalculatedStyle style = Mockito.mock(CalculatedStyle.class);
        onlyFunction(CSSName.WORD_WRAP, style::getIdent, style, IdentValue.NOWRAP);

        CellStyleKey key = new CellStyleKey();
        translator.translateCssToXlsx(style, key);

        assertFalse(key.isWordWrap());
    }

    @Test
    public void testWrap() {
        CalculatedStyle style = Mockito.mock(CalculatedStyle.class);
        onlyFunction(CSSName.WORD_WRAP, style::getIdent, style, IdentValue.BREAK_WORD);

        CellStyleKey key = new CellStyleKey();
        translator.translateCssToXlsx(style, key);

        assertTrue(key.isWordWrap());
    }
}