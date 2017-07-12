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

import com.github.jknack.handlebars.Options;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Conditional helper to check 2 objects equality.<br>
 * Equality is checking using {@link Object#equals}.<br>
 * If both of them are <code>null</code> then they are equal.
 * <pre>
 *   {{if (eq first second}}
 *      {{first}} equals {{second}}
 *   {{else}}
 *      {{first}} is not equal to {{second}}
 *   {{/if}}
 * </pre>
 *
 * @author puvarov
 */
@Component
public class EqualsHelper extends ConditionalHelpers {
    public EqualsHelper() {
        super("eq");
    }

    @Override
    public boolean test(Object context, Options options) throws IOException {
        Object parameter = options.param(0, null);
        if (context == null) {
            return parameter == null;
        }
        return parameter!= null && parameter.equals(context);
    }
}
