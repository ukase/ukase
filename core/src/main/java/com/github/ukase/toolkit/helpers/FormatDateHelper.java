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

package com.github.ukase.toolkit.helpers;

import com.github.jknack.handlebars.Options;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

@Component
public class FormatDateHelper extends AbstractHelper<Object> {
    private static final String HELPER_NAME = "format_date";
    private static final Pattern DATE_ONLY = Pattern.compile("^\\d+.\\d+.\\d+$");
    private static final Pattern DATE_TIME = Pattern.compile("^\\d+.\\d+.\\d+ \\d+:\\d+$");
    private static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm";
    private static final String PARAMETER_FORMAT = "parseFormat";
    private static final String EMPTY_VALUE_MODE = "mode";
    static final String DATE_FORMAT = "dd.MM.yyyy";

    public FormatDateHelper() {
        super(HELPER_NAME);
    }

    @Override
    public Object apply(Object context, Options options) throws IOException {
        if (context instanceof Number) {
            return apply((Number) context, options);
        }
        if (context instanceof CharSequence) {
            return apply(context.toString(), options);
        }
        return apply(options);
    }

    private Object apply(Number context, Options options) {
        return format(new Date(context.longValue()), options);
    }

    private Object apply(String context, Options options) {
        String parseFormat = options.hash(PARAMETER_FORMAT);
        if (parseFormat == null) {
            if (DATE_ONLY.matcher(context.trim()).matches()) {
                parseFormat = DATE_FORMAT;
            } else if (DATE_TIME.matcher(context.trim()).matches()) {
                parseFormat = DATE_TIME_FORMAT;
            }
        }
        if (parseFormat == null) {
            return "";
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(parseFormat);
        try {
            Date date = simpleDateFormat.parse(context);
            return format(date, options);
        } catch (ParseException e) {
            return "";
        }
    }

    private Object apply(Options options) {
        FormatDateMode mode = FormatDateMode.getMode(options.hash.get(EMPTY_VALUE_MODE));
        if (mode == FormatDateMode.STRICT) {
            throw new IllegalArgumentException("For current field were enabled strict mode, but no value got");
        } else if (mode == FormatDateMode.GENERATE) {
            return format(new Date(), options);
        }
        return "";
    }

    private String format(Date date, Options options) {
        String format = options.param(0, "");
        if (format.trim().length() == 0) {
            return "";
        }

        return new SimpleDateFormat(format).format(date);
    }

    private enum FormatDateMode {
        NORMAL, STRICT, GENERATE;

        static FormatDateMode getMode(Object mode) {
            if (mode == null) {
                return NORMAL;
            }
            String upperCaseMode = mode.toString().toUpperCase();
            if (upperCaseMode.equals(STRICT.name())) {
                return STRICT;
            } else if (upperCaseMode.equals(GENERATE.name())) {
                return GENERATE;
            }
            return NORMAL;
        }
    }
}
