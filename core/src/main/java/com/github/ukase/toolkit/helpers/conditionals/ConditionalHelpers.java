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
import com.github.ukase.toolkit.helpers.AbstractHelper;

import java.io.IOException;

/**
 * Abstract class as root class for conditional helpers to check some conditions inside if/unless helper
 * @author puvarov
 */
abstract class ConditionalHelpers extends AbstractHelper<Object> {
    ConditionalHelpers(String name) {
        super(name);
    }

    @Override
    public Object apply(Object context, Options options) throws IOException {
        return test(context, options) ? "true" : null;
    }

    public abstract boolean test(Object context, Options options) throws IOException;
}
