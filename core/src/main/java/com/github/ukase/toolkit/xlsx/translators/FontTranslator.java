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

import org.xhtmlrenderer.css.style.FSDerivedValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class FontTranslator {
    private static final Collection<String> BOLD_VALUES;
    static {
        Collection<String> boldValues = new ArrayList<>();
        boldValues.add("bold");
        BOLD_VALUES = Collections.unmodifiableCollection(boldValues);
    }

    public static boolean isBold(FSDerivedValue fontWeight) {
        return BOLD_VALUES.contains(fontWeight.asString().toLowerCase());
    }

    public static short fontSizePt(FSDerivedValue fontSize) {
        String ptSize = fontSize.asString();
        ptSize = ptSize.substring(0, ptSize.length() - 2);
        return Short.parseShort(ptSize);
    }

    public static boolean isFontSizeSet(FSDerivedValue fontSize) {
        //currently we supports only pt value type
        return fontSize != null && fontSize.asString().endsWith("pt");
    }

    public static boolean isFontWeightSet(FSDerivedValue fontWeight) {
        return fontWeight != null;
    }
}
