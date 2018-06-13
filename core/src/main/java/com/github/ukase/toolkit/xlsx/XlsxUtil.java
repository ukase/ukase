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

import java.util.regex.Pattern;

final class XlsxUtil {
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

    static int greaterInt(Integer first, int second) {
        if (first != null && first >= second) {
            return first;
        }
        return second;
    }
}
