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
import java.util.Arrays;
import java.util.Objects;

/**
 * Conditional helper that checks if first passed object equals to any other.<br/>
 * Equality is checking using {@link Object#equals}.<br/>
 * In case if <code>first</code> is <code>null</code> it will be checked if any other object is <code>null</code>.
 * <pre>
 *   {{if (in first second third fourth ...}}
 *      some of {{...}}, {{fourth}}, {{third}}, {{second}} is equals to {{first}}
 *   {{else}}
 *      {{first}} is not equal to {{second}}
 *   {{/if}}
 * </pre>
 *
 * @author puvarov
 */
@Component
public class InHelper extends ConditionalHelpers {
    public InHelper() {
        super("in");
    }

    @Override
    public boolean test(Object context, Options options) throws IOException {
        if (context == null) {
            return Arrays.stream(options.params).anyMatch(Objects::isNull);
        }
        return Arrays.stream(options.params).filter(Objects::nonNull)
                .anyMatch(context::equals);
    }
}
