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
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.FSDerivedValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Component
public class FontSizeTranslator implements Translator {
    @Override
    public void translateCssToXlsx(CalculatedStyle style, CellStyleKey key) {
        FSDerivedValue fontSize = style.valueByName(CSSName.FONT_SIZE);
        if (isFontSizeSet(fontSize)) {
            key.setFontSize(fontSizePt(fontSize));
        }
    }

    private short fontSizePt(FSDerivedValue fontSize) {
        String ptSize = fontSize.asString();
        ptSize = ptSize.substring(0, ptSize.length() - 2);
        return Short.parseShort(ptSize);
    }

    private boolean isFontSizeSet(FSDerivedValue fontSize) {
        //currently we supports only pt value type
        return fontSize != null && fontSize.asString().endsWith("pt");
    }
}
