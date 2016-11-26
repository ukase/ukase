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
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.FSDerivedValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Component
public class FontWeightTranslator implements Translator {
    private static final Collection<String> BOLD_VALUES;
    static {
        Collection<String> boldValues = new ArrayList<>();
        boldValues.add("bold");
        BOLD_VALUES = Collections.unmodifiableCollection(boldValues);
    }

    @Override
    public void translateCssToXlsx(CalculatedStyle style, CellStyleKey key) {
        FSDerivedValue fontWeight = style.valueByName(CSSName.FONT_WEIGHT);
        if (isFontWeightSet(fontWeight)) {
            key.setBold(isBold(fontWeight));
        }
    }

    private boolean isBold(FSDerivedValue fontWeight) {
        return BOLD_VALUES.contains(fontWeight.asString().toLowerCase());
    }

    private boolean isFontWeightSet(FSDerivedValue fontWeight) {
        return fontWeight != null;
    }
}
