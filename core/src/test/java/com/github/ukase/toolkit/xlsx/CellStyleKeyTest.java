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

package com.github.ukase.toolkit.xlsx;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class CellStyleKeyTest extends MockedTests {
    @Test
    public void failOnSetNullBorder() {
        CellStyleKey cellStyleKey = new CellStyleKey();

        trySetNull(cellStyleKey::setBorderTop);
        trySetNull(cellStyleKey::setBorderBottom);
        trySetNull(cellStyleKey::setBorderLeft);
        trySetNull(cellStyleKey::setBorderRight);
    }

    @Test
    public void failOnSetNullAlignment() {
        CellStyleKey cellStyleKey = new CellStyleKey();

        trySetNull(cellStyleKey::setHorizontalAlignment);
        trySetNull(cellStyleKey::setVerticalAlignment);
    }

    @Test
    public void testApplyToStyleBorders1() throws Exception {
        CellStyleKey cellStyleKey = new CellStyleKey();
        cellStyleKey.setBorderTop(BorderStyle.DASH_DOT);
        cellStyleKey.setBorderBottom(BorderStyle.DASH_DOT_DOT);
        cellStyleKey.setBorderLeft(BorderStyle.DASHED);

        XSSFCellStyle mockedStyle = mock(XSSFCellStyle.class);
        onlyValue(BorderStyle.DASH_DOT, mockedStyle::setBorderTop, mockedStyle);
        onlyValue(BorderStyle.DASH_DOT_DOT, mockedStyle::setBorderBottom, mockedStyle);
        onlyValue(BorderStyle.DASHED, mockedStyle::setBorderLeft, mockedStyle);
        onlyValue(BorderStyle.NONE, mockedStyle::setBorderRight, mockedStyle);

        cellStyleKey.applyToStyle(mockedStyle, null);
    }
    @Test
    public void testApplyToStyleBorders2() throws Exception {
        CellStyleKey cellStyleKey = new CellStyleKey();
        cellStyleKey.setBorderTop(BorderStyle.MEDIUM);
        cellStyleKey.setBorderBottom(BorderStyle.MEDIUM_DASH_DOT);
        cellStyleKey.setBorderLeft(BorderStyle.MEDIUM_DASHED);
        cellStyleKey.setBorderRight(BorderStyle.THICK);

        XSSFCellStyle mockedStyle = mock(XSSFCellStyle.class);
        onlyValue(BorderStyle.MEDIUM, mockedStyle::setBorderTop, mockedStyle);
        onlyValue(BorderStyle.MEDIUM_DASH_DOT, mockedStyle::setBorderBottom, mockedStyle);
        onlyValue(BorderStyle.MEDIUM_DASHED, mockedStyle::setBorderLeft, mockedStyle);
        onlyValue(BorderStyle.THICK, mockedStyle::setBorderRight, mockedStyle);

        cellStyleKey.applyToStyle(mockedStyle, null);
    }

}