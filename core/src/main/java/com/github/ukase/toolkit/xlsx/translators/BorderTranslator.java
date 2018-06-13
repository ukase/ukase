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

import org.apache.poi.ss.usermodel.BorderStyle;
import org.xhtmlrenderer.css.constants.IdentValue;

public abstract class BorderTranslator implements Translator {
    private static final String DASHED = "dashed";
    private static final String DOTTED = "dotted";

    BorderStyle prepareBorder(float width, IdentValue ident) {
        if (width >= 40.0F) {
            if (isDashed(ident)) {
                return BorderStyle.MEDIUM_DASHED;
            } else if (isDotted(ident)) {
                return BorderStyle.MEDIUM_DASH_DOT;
            }
            return BorderStyle.THICK;
        } else if (width >= 20.0F) {
            if (isDashed(ident)) {
                return BorderStyle.DASHED;
            } else if (isDotted(ident)) {
                return BorderStyle.DOTTED;
            }
            return BorderStyle.MEDIUM;
        } else if (width >= 0.1F) {
            if (isDotted(ident)) {
                return BorderStyle.HAIR;
            }
            return BorderStyle.THIN;
        } else {
            return BorderStyle.NONE;
        }
    }

    private boolean isDashed(IdentValue ident) {
        return isIdentValue(ident, DASHED);
    }

    private boolean isDotted(IdentValue ident) {
        return isIdentValue(ident, DOTTED);
    }

    private boolean isIdentValue(IdentValue ident, String value) {
        return value.equals(ident.asString());
    }

}
