/*
 * Copyright (c) 2015 Konstantin Lepa <konstantin+ukase@lepabox.net>
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

package com.github.ukase.toolkit.helpers;

import com.github.jknack.handlebars.Options;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SubStringHelper extends AbstractHelper<CharSequence> {
    private static final String HELPER_NAME = "substring";
    private static final int NO_END = -1;

    public SubStringHelper() {
        super(HELPER_NAME);
    }

    @Override
    public Object apply(CharSequence context, Options options) throws IOException {
        if (context == null) {
            return "";
        }
        String stringValue = context.toString();
        int length = stringValue.length();

        int start = start(options.param(0, -1), length);
        int end = end(options.param(1, -1), length);

        if (end == NO_END) {
            return stringValue.substring(start);
        }

        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }
        return stringValue.substring(start, end);
    }

    private int start(int start, int length) {
        if (start < 0) {
            return 0;
        }
        return Math.min(length, start);
    }

    private int end(int end, int length) {
        if (end < 0) {
            return NO_END;
        }
        return Math.min(length, end);
    }
}
