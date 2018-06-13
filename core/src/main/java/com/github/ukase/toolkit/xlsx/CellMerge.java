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

package com.github.ukase.toolkit.xlsx;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

class CellMerge {
    private final int cellStart;
    private final int cellEnd;
    private final int rowStart;
    private final int rowEnd;
    private final CellStyle style;

    CellMerge(int cellStart, int cellWidth, int rowStart, int rowWidth, CellStyle style) {
        this.rowEnd = rowStart + rowWidth - 1;
        this.cellEnd = cellStart + cellWidth - 1;
        this.cellStart = cellStart;
        this.rowStart = rowStart;
        this.style = style;
    }

    boolean isApplicable(Row row) {
        return isApplicableRow(row.getRowNum())
                && isApplicableCell(row.getPhysicalNumberOfCells());
    }

    void fillRow(Row row) {
        int rowNumber;
        while((rowNumber = row.getPhysicalNumberOfCells()) <= cellEnd) {
            row.createCell(rowNumber)
                    .setCellStyle(style);
        }
    }

    void apply(Sheet sheet) {
        CellRangeAddress region = new CellRangeAddress(rowStart, rowEnd, cellStart, cellEnd);
        sheet.addMergedRegion(region);
    }

    private boolean isApplicableRow(int rowNumber) {
        return rowNumber >= rowStart && rowNumber <= rowEnd;
    }

    private boolean isApplicableCell(int cellNumber) {
        return cellNumber >= cellStart && cellNumber <= cellEnd;
    }
}
