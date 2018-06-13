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

import com.github.jknack.handlebars.Helper;
import com.github.ukase.config.UkaseSettings;
import com.github.ukase.toolkit.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = {"ukase.enabled-sources.jar"}, havingValue = "true")
public class JarSource implements Source {
    private final Map<String, String> helpers = new HashMap<>();
    private final Map<String, Helper<?>> helpersInstances = new HashMap<>();
    private final URLClassLoader classLoader;
    private final URL jar;
    private final ZipTemplateLoader templateLoader;

    @Autowired
    public JarSource(UkaseSettings settings, ZipTemplateLoader templateLoader) throws IOException {
        this.templateLoader = templateLoader;

        if (settings.getJar() == null) {
            throw new IllegalStateException("Can't load jar file, while JarSource is enabled by configuration");
        }

        jar = settings.getJar().toURI().toURL();

        Collection<String> helperConfigurationFiles = templateLoader.getResources(IS_HELPERS_CONFIGURATION);

        Properties properties = new Properties();
        helperConfigurationFiles.stream().
                map(templateLoader::getResource).
                filter(Objects::nonNull).
                forEach(stream -> loadStreamToProperties(stream, properties));
        properties.forEach(this::registerHelper);

        if (hasHelpers()) {
            URL[] jars = new URL[] {jar};
            classLoader = new URLClassLoader(jars, getClass().getClassLoader());
            helpers.forEach((name, className) -> helpersInstances.put(name, getHelper(className)));
        } else {
            classLoader = null;
        }
    }

    public Map<String, Helper<?>> getHelpers() {
        return Collections.unmodifiableMap(helpersInstances);
    }

    @Override
    public boolean hasResource(String url) {
        return getResource(url) != null;
    }

    @Override
    public InputStream getResource(String url) {
        return templateLoader.getResource(url);
    }

    @Override
    public Collection<String> getFontsUrls() {
        if (jar != null) {
            return templateLoader.getResources(IS_FONT).stream()
                                 .map(font -> "jar:" + jar + "!/" + font)
                                 .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public int order() {
        return ORDER_JAR;
    }

    private Helper<?> getHelper(String className) {
        try {
            Class<?> clazz = Class.forName(className, true, classLoader);
            Class<? extends Helper> helperClass = clazz.asSubclass(Helper.class);
            return helperClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Wrong configuration", e);
        }
    }

    private void registerHelper(Object name, Object className) {
        helpers.put((String) name, (String) className);
    }

    private void loadStreamToProperties(InputStream stream, Properties properties) {
        try {
            properties.load(stream);
        } catch (IOException e) {
            throw new IllegalStateException("Wrong configuration", e);
        }
    }

    private boolean hasHelpers() {
        return !helpers.isEmpty();
    }
}
