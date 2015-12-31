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

package com.github.ukase.toolkit.jar;

import com.github.jknack.handlebars.Helper;
import com.github.ukase.config.UkaseSettings;
import com.github.ukase.toolkit.CompoundTemplateLoader;
import lombok.Getter;
import com.github.ukase.toolkit.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;

@Component
public class JarSource implements Source {
    @Getter
    private final CompoundTemplateLoader templateLoader;
    private final Map<String, String> helpers = new HashMap<>();
    private final Map<String, Helper<?>> helpersInstances = new HashMap<>();
    private final URLClassLoader classLoader;
    private final Collection<String> fonts;

    @Autowired
    public JarSource(CompoundTemplateLoader templateLoader, UkaseSettings settings) {
        this.templateLoader = templateLoader;

        if (settings.getJar() == null) {
            classLoader = null;
            fonts = Collections.emptyList();

            return;
        }

        try {
            ZipEntry resourcesEntry = templateLoader.getResource("imported-handlers.properties");
            if (resourcesEntry != null) {
                Properties properties = new Properties();
                properties.load(templateLoader.getResource(resourcesEntry));
                properties.forEach(this::registerHelper);
            }

            fonts = findFonts(this.templateLoader);

            if (hasHelpers()) {
                URL jar = settings.getJar().toURI().toURL();
                URL[] jars = new URL[] {jar};
                classLoader = new URLClassLoader(jars, getClass().getClassLoader());
                helpers.forEach((name, className) -> helpersInstances.put(name, getHelper(className)));
            } else {
                classLoader = null;
            }

        } catch (IOException e) {
            throw new IllegalStateException("Wrong configuration", e);
        }
    }

    @Override
    public Map<String, Helper<?>> getHelpers() {
        return new HashMap<>(helpersInstances);
    }

    @Override
    public boolean hasHelpers() {
        return !helpers.isEmpty();
    }

    @Override
    public boolean hasResource(String url) {
        return templateLoader.hasResource(url);
    }

    @Override
    public boolean hasTemplate(String name) {
        return templateLoader.hasResource(name + ".hbs");
    }

    @Override
    public InputStream getResource(String url) throws IOException {
        return templateLoader.getResource(templateLoader.getResource(url));
    }

    @Override
    public Collection<String> getFontsUrls() {
        return Collections.unmodifiableCollection(fonts);
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

    private static Collection<String> findFonts(CompoundTemplateLoader templateLoader) {
        return templateLoader.getResources(IS_FONT);
    }
}
