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
import org.hamcrest.core.IsNot;
import org.junit.Test;

import java.util.function.Consumer;

import static org.mockito.Mockito.*;

public class CellStyleKeyTest {
    @Test
    public void failOnSetNullBorder() {
        CellStyleKey cellStyleKey = new CellStyleKey();

        trySetNull(cellStyleKey::setBorderTop);
        trySetNull(cellStyleKey::setBorderBottom);
        trySetNull(cellStyleKey::setBorderLeft);
        trySetNull(cellStyleKey::setBorderRight);
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

    private static <T> void trySetNull(Consumer<T> consumer) {
        try {
            consumer.accept(null);
            throw new IllegalStateException("Null were successfully consumed - but shouldn't!");
        } catch (NullPointerException e) {
            //all ok;
        }
    }

    private static <T> void onlyValue(T value, Consumer<T> consumer, Object mock) {
        String wrongArgument = "Wrong argument - wait for " + value + " but got another";
        doNothing().when(mock);
        consumer.accept(value);
        doThrow(exception(wrongArgument)).when(mock);
        consumer.accept(not(value));
    }

    private static <T> T not(T object) {
        return argThat(IsNot.not(object));
    }

    private static IllegalArgumentException exception(String message) {
        return new IllegalArgumentException(message);
    }
}