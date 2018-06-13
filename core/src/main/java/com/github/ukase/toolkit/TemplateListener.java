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

package com.github.ukase.toolkit;

import java.util.function.Predicate;
import java.util.function.Consumer;

public interface TemplateListener {
    void resourceUpdated(String resourceName);

    static TemplateListener templateListener(String templateName, Consumer<Boolean> consumer) {
        Predicate<String> predicate;
        if (templateName.equals("ANY")) {
            predicate = resourceName -> true;
        } else {
            predicate = resourceName -> !resourceName.endsWith("hbs") || resourceName.equals(templateName + ".hbs");
        }

        return resourceName -> consumer.accept(predicate.test(resourceName));
    }
}
