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

import org.springframework.stereotype.Component;

/**
 * Conditional helper for comparable types (line numbers).<br>
 * Checks if <code>first &lt; second</code>
 * <pre>
 *   {{if (lt first second}}
 *      {{first}} &lt; {{second}}
 *   {{else}}
 *      {{first}} &gt;= {{second}}
 *   {{/if}}
 * </pre>
 *
 * @author puvarov
 */
@Component
public class LessThanHelper extends ComparableSubHelper {
    public LessThanHelper() {
        super("lt");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean compare(Comparable object, Comparable against) {
        return object.compareTo(against) < 0;
    }
}
