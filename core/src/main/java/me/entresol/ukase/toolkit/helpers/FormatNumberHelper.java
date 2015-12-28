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

package me.entresol.ukase.toolkit.helpers;

import com.github.jknack.handlebars.Options;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.DecimalFormat;

@Component
public class FormatNumberHelper extends AbstractHelper<Long> {
    private static final String HELPER_NAME = "format_number";

    public FormatNumberHelper() {
        super(HELPER_NAME);
    }

    @Override
    public CharSequence apply(Long context, Options options) throws IOException {
        if (context == null) {
            return "";
        }
        String format = options.param(0, "");
        if (format.trim().length() == 0) {
            return "";
        }

        return new DecimalFormat(format).format(context);
    }
}
