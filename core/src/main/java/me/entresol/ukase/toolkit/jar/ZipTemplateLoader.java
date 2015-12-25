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

package me.entresol.ukase.toolkit.jar;

import com.github.jknack.handlebars.io.AbstractTemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;
import me.entresol.ukase.config.UkaseSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
@Scope("prototype")
class ZipTemplateLoader extends AbstractTemplateLoader {
    private final ZipFile zip;
    private final Map<String, ZipEntry> resources = new HashMap<>();

    @Autowired
    public ZipTemplateLoader(UkaseSettings settings) throws IOException {
        zip = new ZipFile(settings.getResources());
        zip.stream().forEach(this::registerResource);
    }

    @Override
    public TemplateSource sourceAt(String location) throws IOException {
        return new ZipTemplateSource(zip, zip.getEntry(location));
    }

    boolean hasResource(String location) {
        return resources.containsKey(location);
    }

    ZipEntry getResource(String location) {
        return resources.get(location);
    }

    InputStream getResource(ZipEntry resource) throws IOException {
        return zip.getInputStream(resource);
    }

    Collection<String> getResources(Predicate<String> filter) {
        return resources.keySet().stream().filter(filter).collect(Collectors.toList());
    }

    private void registerResource(ZipEntry entry) {
        resources.put(entry.getName(), entry);
    }
}
