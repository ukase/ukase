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

import org.springframework.stereotype.Component;

/**
 * Conditional helper for comparable types (line numbers).<br>
 * Checks if <code>first &gt;= second</code>
 * <pre>
 *   {{if (lt first second}}
 *      {{first}} &gt;= {{second}}
 *   {{else}}
 *      {{first}} &lt; {{second}}
 *   {{/if}}
 * </pre>
 *
 * @author puvarov
 */
@Component
public class GreaterThanOrEqualToHelper extends ComparableSubHelper {
    public GreaterThanOrEqualToHelper() {
        super("gte");
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean compare(Comparable object, Comparable against) {
        return object.compareTo(against) >= 0;
    }
}
