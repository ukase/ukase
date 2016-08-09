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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class CompoundSource implements Source{
    private final JarSource jarSource;
    private final FileSource fileSource;
    private final Collection<String> fonts = new CopyOnWriteArraySet<>();

    @Autowired
    public CompoundSource(JarSource jarSource, FileSource fileSource) {
        this.jarSource = jarSource;
        this.fileSource = fileSource;
    }

    @Override
    public void registerListener(SourceListener listener) {
        fileSource.registerListener(listener);
    }

    @Override
    public Collection<String> getFontsUrls() {
        fonts.addAll(fileSource.getFontsUrls());
        fonts.addAll(jarSource.getFontsUrls());
        return fonts;
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
}
