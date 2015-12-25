/*
 * Copyright (c) 2015 Konstantin Lepa <konstantin+ukase@lepabox.net>
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

package me.entresol.ukase.toolkit;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.io.TemplateLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

public interface Source {
    Predicate<String> IS_FONT = fileName -> fileName.toLowerCase().endsWith("ttf");

    TemplateLoader getTemplateLoader();
    Map<String, Helper<?>> getHelpers();
    boolean hasHelpers();
    boolean hasResource(String url);
    InputStream getResource(String url) throws IOException;
    Collection<String> getFontsUrls();
}
