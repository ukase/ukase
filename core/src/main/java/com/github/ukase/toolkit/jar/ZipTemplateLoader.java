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

package com.github.ukase.toolkit.jar;

import com.github.jknack.handlebars.io.AbstractTemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;
import com.github.ukase.config.UkaseSettings;
import com.github.ukase.toolkit.UkaseTemplateLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(name = {"ukase.enabled-sources.jar"}, havingValue = "true")
public class ZipTemplateLoader extends AbstractTemplateLoader implements UkaseTemplateLoader {
    private final Map<String, ZipEntry> resources = new HashMap<>();
    private final ZipFile zip;

    @Autowired
    public ZipTemplateLoader(UkaseSettings settings) throws IOException {
        if (settings.getJar() == null) {
            throw new IllegalStateException("Wrong configuration - not set jar file, but jar source is enabled");
        }

        zip = new ZipFile(settings.getJar());
        zip.stream().forEach(this::registerResource);
    }

    @Override
    public TemplateSource sourceAt(String location) throws IOException {
        location = resolve(location);
        if (location.startsWith("/")) {
            location = location.substring(1);
        }
        ZipEntry entry = zip.getEntry(location);
        if (entry == null) {
            return null;
        }
        return new ZipTemplateSource(zip, entry);
    }

    @Override
    public boolean hasTemplate(String name) {
        try {
            return sourceAt(name) != null;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public int order() {
        return ORDER_JAR;
    }

    Collection<String> getResources(Predicate<String> filter) {
        return resources.keySet().stream().filter(filter).collect(Collectors.toList());
    }

    InputStream getResource(String location) throws IllegalStateException {
        ZipEntry resource = resources.get(location);
        if (resource == null) {
            return null;
        }
        return getResource(resource);
    }

    private InputStream getResource(ZipEntry resource) throws IllegalStateException {
        try {
            return zip.getInputStream(resource);
        } catch (IOException e) {
            throw new IllegalStateException("Wrong configuration", e);
        }
    }

    private void registerResource(ZipEntry entry) {
        resources.put(entry.getName(), entry);
    }
}
