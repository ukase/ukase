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

import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.util.Arrays;

public enum VerticalAlignmentTranslator {
    TOP(VerticalAlignment.TOP), MIDDLE(VerticalAlignment.CENTER), BOTTOM(VerticalAlignment.BOTTOM);

    private final VerticalAlignment xssfAlignment;

    VerticalAlignmentTranslator(org.apache.poi.ss.usermodel.VerticalAlignment xssfAlignment) {
        this.xssfAlignment = xssfAlignment;
    }

    private VerticalAlignment getXssfAlignment() {
        return xssfAlignment;
    }

    public static VerticalAlignment translate(Object value) {
        if (value == null) {
            return null;
        }
        String upperCaseValue = value.toString().toUpperCase();
        return Arrays.stream(values())
                .filter(alignment -> alignment.name().equals(upperCaseValue))
                .map(VerticalAlignmentTranslator::getXssfAlignment)
                .findAny().orElse(null);
    }
}
