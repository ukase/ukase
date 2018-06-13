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
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.FSDerivedValue;

import java.util.Arrays;

@Component
public class VerticalAlignmentTranslator implements Translator {

    @Override
    public void translateCssToXlsx(CalculatedStyle style, CellStyleKey key) {
        FSDerivedValue value = style.valueByName(CSSName.VERTICAL_ALIGN);
        key.setVerticalAlignment(Alignment.translate(value));
    }

    private enum Alignment {
        TOP(VerticalAlignment.TOP), MIDDLE(VerticalAlignment.CENTER), BOTTOM(VerticalAlignment.BOTTOM);

        private final VerticalAlignment alignment;

        Alignment(VerticalAlignment xssfAlignment) {
            this.alignment = xssfAlignment;
        }

        private VerticalAlignment getAlignment() {
            return alignment;
        }

        static VerticalAlignment translate(Object value) {
            if (value == null) {
                return VerticalAlignment.BOTTOM;
            }
            String upperCaseValue = value.toString().toUpperCase();
            return Arrays.stream(values())
                    .filter(alignment -> alignment.name().equals(upperCaseValue))
                    .map(Alignment::getAlignment)
                    .findAny().orElse(VerticalAlignment.BOTTOM);
        }
    }
}
