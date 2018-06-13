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

package com.github.ukase.toolkit.helpers.conditionals;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

/**
 * Conditional helper. Provides possibility to compose conditions in single with logical <code>and</code> operand
 * <pre>
 *   {{if (and (eq first second} (ne first second)}
 *      this never happens
 *   {{else}}
 *      so this will be printed
 *   {{/if}}
 * </pre>
 *
 * @author puvarov
 */
@Component
public class AndHelper extends ConditionalHelpers {
    public AndHelper() {
        super("and");
    }

    @Override
    public boolean test(Object context, Options options) throws IOException {
        boolean testParameters = Arrays.stream(options.params).noneMatch(Handlebars.Utils::isEmpty);
        return testParameters && !Handlebars.Utils.isEmpty(context);
    }
}
