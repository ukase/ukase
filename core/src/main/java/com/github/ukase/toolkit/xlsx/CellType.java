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

import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;

@Getter
public enum CellType {
    STRING(Cell.CELL_TYPE_STRING, "string"),
    NUMERIC(Cell.CELL_TYPE_NUMERIC, "numeric"),
    DATE(Cell.CELL_TYPE_STRING, "date"),
    DEFAULT(Cell.CELL_TYPE_BLANK, "common");

    private int xssfType;
    private String stringValue;

    CellType(int xssfType, String stringValue) {
        this.xssfType = xssfType;
        this.stringValue = stringValue;
    }

    public static CellType fromString(String type) {
        if (type == null || type.isEmpty()) {
            return DEFAULT;
        }
        for (CellType cellType: values()) {
            if (cellType.stringValue.equals(type)) {
                return cellType;
            }
        }
        return DEFAULT;
    }
}
