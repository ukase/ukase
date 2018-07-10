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
import com.github.ukase.config.properties.FormatDateProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.regex.Pattern;

@Component
public class FormatDateHelper extends AbstractHelper<Object> {
    private static final String HELPER_NAME = "format_date";
    //private static final Pattern DATE_TIME = Pattern.compile("^\\d+.\\d+.\\d+ \\d+:\\d+$");
    //private static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm";
    private static final String PARAMETER_FORMAT = "parseFormat";
    private static final String EMPTY_VALUE_MODE = "mode";

    static final String DATE_FORMAT = "dd.MM.yyyy";

    private final Pattern datePattern;
    private final DateTimeFormatter dateParser;
    private final DateTimeFormatter dateFormatter;
    private final boolean disablePatterns;

    @Autowired
    public FormatDateHelper(FormatDateProperties properties) {
        super(HELPER_NAME);

        dateParser = new DateTimeFormatterBuilder()
                .appendPattern(properties.getParseFormat())
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .toFormatter();
        disablePatterns = properties.isDisablePatterns();
        if (disablePatterns) {
            datePattern = null;
        } else {
            datePattern = Pattern.compile(properties.getDatePattern());
        }
        dateFormatter = DateTimeFormatter.ofPattern(properties.getFormatDate());
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
        Instant instant = Instant.ofEpochMilli(context.longValue());
        LocalDateTime date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return format(date, options);
    }

    private Object apply(String context, Options options) {
        String parameterFormat = options.hash(PARAMETER_FORMAT);
        if (parameterFormat != null) {
            parameterFormat = parameterFormat.trim();
        }

        DateTimeFormatter parseFormat;
        if (parameterFormat == null || parameterFormat.isEmpty()) {
            parseFormat = getConfigDateParseFormat(context);
            if (parseFormat == null) {
                return "";
            }
        } else {
            parseFormat = DateTimeFormatter.ofPattern(parameterFormat);
        }

        try {
            LocalDateTime date = parseFormat.parse(context, LocalDateTime::from);
            return format(date, options);
        } catch (DateTimeParseException e) {
            return "";
        }
    }

    private Object apply(Options options) {
        FormatDateMode mode = FormatDateMode.getMode(options.hash.get(EMPTY_VALUE_MODE));
        if (mode == FormatDateMode.STRICT) {
            throw new IllegalArgumentException("For current field were enabled strict mode, but no value got");
        } else if (mode == FormatDateMode.GENERATE) {
            return format(LocalDateTime.now(), options);
        }
        return "";
    }

    private String format(LocalDateTime date, Options options) {
        String format = options.param(0, "");
        DateTimeFormatter formatter;
        if (format.trim().length() == 0) {
            formatter = dateFormatter;
        } else {
            formatter = DateTimeFormatter.ofPattern(format);
        }

        return formatter.format(date);
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

    private DateTimeFormatter getConfigDateParseFormat(String trimmedContext) {
        if (trimmedContext == null) {
            return null;
        }
        trimmedContext = trimmedContext.trim();
        if (trimmedContext.isEmpty()) {
            return null;
        }
        if (disablePatterns) {
            return dateParser;
        }
        //if (datePattern.matcher(trimmedContext).matches()) {
        //    return dateParser;
        //} else
        if (datePattern.matcher(trimmedContext).matches()) {
            return dateParser;
        }
        return null;
    }
}
