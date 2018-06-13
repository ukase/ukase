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
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.style.CalculatedStyle;

@Component
public class HorizontalAlignmentTranslator implements Translator {
    private static final String JUSTIFY = "justify";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";

    @Override
    public void translateCssToXlsx(CalculatedStyle style, CellStyleKey key) {
        IdentValue ident = style.getIdent(CSSName.TEXT_ALIGN);
        HorizontalAlignment alignment;
        if (isLeft(ident)) {
            alignment = HorizontalAlignment.LEFT;
        } else if (isRight(ident)) {
            alignment = HorizontalAlignment.RIGHT;
        } else if (isJustify(ident)) {
            alignment = HorizontalAlignment.JUSTIFY;
        } else {
            alignment = HorizontalAlignment.CENTER;
        }

        key.setHorizontalAlignment(alignment);
    }

    private boolean isJustify(IdentValue ident) {
        return isIdentValue(ident, JUSTIFY);
    }

    private boolean isLeft(IdentValue ident) {
        return isIdentValue(ident, LEFT);
    }

    private boolean isRight(IdentValue ident) {
        return isIdentValue(ident, RIGHT);
    }

    private boolean isIdentValue(IdentValue ident, String value) {
        return value.equals(ident.asString());
    }
}
