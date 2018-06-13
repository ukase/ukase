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

import com.github.ukase.toolkit.xlsx.MockedTests;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.newtable.CollapsedBorderValue;

abstract class BorderTranslatorTests extends MockedTests {
    static final CollapsedBorderValue NONE_BORDER = getBorderValue(IdentValue.NONE);
    static final CollapsedBorderValue DASHED = getBorderValue(IdentValue.DASHED);
    static final CollapsedBorderValue DOTTED = getBorderValue(IdentValue.DOTTED);

    private static CollapsedBorderValue getBorderValue(IdentValue ident) {
        return new CollapsedBorderValue(ident, 0, null, 0);
    }
}
