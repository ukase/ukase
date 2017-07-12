/*
 * Copyright (c) 2017 Konstantin Lepa <konstantin+ukase@lepabox.net>
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
 * Conditional helper. Provides possibility to compose conditions in single with logical <code>or</code> operand
 * <pre>
 *   {{if (or (eq first second} (ne first second)}
 *      this happens in any case
 *   {{else}}
 *      so this will not be printed ever
 *   {{/if}}
 * </pre>
 *
 * @author puvarov
 */
@Component
public class OrHelper extends ConditionalHelpers {
    public OrHelper() {
        super("or");
    }

    @Override
    public boolean test(Object context, Options options) throws IOException {
        Object[] params = options.params;
        boolean testParameters = Arrays.stream(params).filter(Handlebars.Utils::isEmpty).count() != params.length;
        return testParameters || !Handlebars.Utils.isEmpty(context);
    }
}
