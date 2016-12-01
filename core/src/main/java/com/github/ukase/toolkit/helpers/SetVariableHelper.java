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

package com.github.ukase.toolkit.helpers;

import com.github.jknack.handlebars.Options;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class SetVariableHelper extends AbstractHelper<Object> {
    private static final String HELPER_NAME = "set_var";

    public SetVariableHelper() {
        super(HELPER_NAME);
    }

    @Override
    public CharSequence apply(Object context, Options options) throws IOException {
        CharSequence generatedBlockData = options.fn();
        if (options.isFalsy(generatedBlockData)) {
            setVariable(options, context);
        } else {
            setVariable(options, StringUtils.trimToEmpty(generatedBlockData.toString()));
        }
        return "";
    }

    private void setVariable(Options options, Object value) throws IOException {
        String name = getVariableName(options);
        Map<String, Object> currentContext = getContext(options);
        if (currentContext == null) {
            return;
        }
        currentContext.put(name, value);
    }

    private String getVariableName(Options options) {
        return options.hash("name");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getContext(Options options) {
        Object possibleContext = options.context.model();
        if (possibleContext instanceof Map) {
            return (Map<String, Object>)possibleContext;
        }
        return null;
    }
}
