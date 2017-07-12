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
public class SubStringHelper extends AbstractHelper<String> {
    private static final String HELPER_NAME = "substring";

    public SubStringHelper() {
        super(HELPER_NAME);
    }

    @Override
    public CharSequence apply(String context, Options options) throws IOException {
        if (context == null) {
            return "";
        }
        Integer start = start(options.param(0, null), context.length());
        Integer end = end(options.param(1, null), context.length());

        if (isIncorrectParameters(context, start, end)) {
            return "";
        }

        if (end == null) {
            return context.substring(start);
        } else {
            return context.substring(start, end);
        }
    }

    private boolean isIncorrectParameters(String context, Integer start, Integer end) {
        return start == null
                || start < 0
                || end != null && (end > context.length() || end < start);
    }

    private int start(Integer start, int length) {
        if (start == null || start < 0) {
            return 0;
        }
        return Math.min(length, start);
    }

    private int end(Integer end, int length) {
        if (end == null || end < 0) {
            return 0;
        }
        return Math.min(length, end);
    }
}
