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
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.style.derived.BorderPropertySet;

import java.util.regex.Pattern;

final class XlsxUtil {
    private static final String DASHED = "dashed";
    private static final String DOTTED = "dotted";
    private static final String JUSTIFY = "justify";
    private static final String LEFT = "left";
    private static final String RIGHT = "right";
    private static final Pattern IS_NUMBER = Pattern.compile("^[0-9]+$");

    private XlsxUtil() {
    }

    static int intValue(String data, int defaultValue) {
        if (data != null
                && IS_NUMBER.matcher(data).matches()) {
            return Integer.parseInt(data);
        }
        return defaultValue;
    }

    static BorderStyle prepareTopBorder(BorderPropertySet border) {
        return prepareBorder(border.top(), border.topStyle());
    }
    static BorderStyle prepareRightBorder(BorderPropertySet border) {
        return prepareBorder(border.right(), border.rightStyle());
    }
    static BorderStyle prepareBottomBorder(BorderPropertySet border) {
        return prepareBorder(border.bottom(), border.bottomStyle());
    }
    static BorderStyle prepareLeftBorder(BorderPropertySet border) {
        return prepareBorder(border.left(), border.leftStyle());
    }

    static HorizontalAlignment prepareAlignment(IdentValue ident) {
        if (XlsxUtil.isLeft(ident)) {
            return HorizontalAlignment.LEFT;
        } else if (XlsxUtil.isRight(ident)) {
            return HorizontalAlignment.RIGHT;
        } else if (XlsxUtil.isJustify(ident)) {
            return HorizontalAlignment.JUSTIFY;
        }
        return HorizontalAlignment.CENTER;
    }

    static int greaterInt(Integer first, int second) {
        if (first != null && first >= second) {
            return first;
        }
        return second;
    }

    private static boolean isDashed(IdentValue ident) {
        return isIdentValue(ident, DASHED);
    }

    private static boolean isDotted(IdentValue ident) {
        return isIdentValue(ident, DOTTED);
    }

    private static boolean isJustify(IdentValue ident) {
        return isIdentValue(ident, JUSTIFY);
    }

    private static boolean isLeft(IdentValue ident) {
        return isIdentValue(ident, LEFT);
    }

    private static boolean isRight(IdentValue ident) {
        return isIdentValue(ident, RIGHT);
    }

    private static boolean isIdentValue(IdentValue ident, String value) {
        return value.equals(ident.asString());
    }

    private static BorderStyle prepareBorder(float width, IdentValue ident) {
        if (width >= 40.0F) {
            if (XlsxUtil.isDashed(ident)) {
                return BorderStyle.MEDIUM_DASHED;
            } else if (XlsxUtil.isDotted(ident)) {
                return BorderStyle.MEDIUM_DASH_DOT;
            }
            return BorderStyle.THICK;
        } else if (width >= 20.0F) {
            if (XlsxUtil.isDashed(ident)) {
                return BorderStyle.DASHED;
            } else if (XlsxUtil.isDotted(ident)) {
                return BorderStyle.DOTTED;
            }
            return BorderStyle.MEDIUM;
        } else if (width >= 0.1F) {
            if (XlsxUtil.isDotted(ident)) {
                return BorderStyle.HAIR;
            }
            return BorderStyle.THIN;
        } else {
            return BorderStyle.NONE;
        }
    }
}
