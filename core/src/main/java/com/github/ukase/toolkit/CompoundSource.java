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

package com.github.ukase.toolkit;

import com.github.jknack.handlebars.Helper;
import com.github.ukase.toolkit.fs.FileSource;
import com.github.ukase.toolkit.jar.JarSource;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class CompoundSource implements Source {
    private final JarSource jarSource;
    private final FileSource fileSource;
    @Getter
    private final Collection<String> fontsUrls;

    @Autowired
    public CompoundSource(JarSource jarSource, FileSource fileSource) {
        this.jarSource = jarSource;
        this.fileSource = fileSource;

        Collection<String> fonts = new HashSet<>(jarSource.getFontsUrls());
        fonts.addAll(fileSource.getFontsUrls());
        addLiberationFonts(fonts);
        this.fontsUrls = Collections.unmodifiableCollection(fonts);
    }

    @Override
    public void registerListener(SourceListener listener) {
        fileSource.registerListener(listener);
    }

    @Override
    public InputStream getResource(String url) throws IOException {
        InputStream stream = fileSource.getResource(url);
        if (stream == null) {
            stream = jarSource.getResource(url);
        }
        return stream;
    }

    @Override
    public boolean hasTemplate(String name) {
        return fileSource.hasTemplate(name) || jarSource.hasTemplate(name);
    }

    @Override
    public boolean hasResource(String url) {
        return fileSource.hasResource(url) || jarSource.hasResource(url);
    }

    @Override
    public boolean hasHelpers() {
        return jarSource.hasHelpers();
    }

    @Override
    public Map<String, Helper<?>> getHelpers() {
        return jarSource.getHelpers();
    }

    String getDefaultFontUrl() {
        return getFontsUrls().stream()
                .filter(this::isRegularFont)
                .findAny().orElse(null);
    }

    private void addLiberationFonts(Collection<String> fonts) {
        ClassLoader loader = getClass().getClassLoader();
        Stream.of("LiberationSerif-Regular.ttf",
                  "LiberationSerif-Bold.ttf",
                  "LiberationSerif-Italic.ttf",
                  "LiberationSerif-BoldItalic.ttf")
                .map(loader::getResource)
                .filter(Objects::nonNull)
                .map(Object::toString)
                .forEach(fonts::add);
    }

    private boolean isRegularFont(String fontName) {
        String name = fontName.toLowerCase();
        return !(name.contains("bold") || name.contains("italic"));
    }
}
