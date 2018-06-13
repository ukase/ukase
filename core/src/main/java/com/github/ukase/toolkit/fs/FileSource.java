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

package com.github.ukase.toolkit.fs;

import com.github.ukase.toolkit.TemplateListenable;
import com.github.ukase.toolkit.TemplateListener;
import com.github.ukase.config.UkaseSettings;
import com.github.ukase.toolkit.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = {"ukase.enabled-sources.fs"}, havingValue = "true")
public class FileSource implements Source, TemplateListenable {
    private final File resources;
    private final FileUpdatesListener resourcesListener;

    @Autowired
    public FileSource(UkaseSettings settings) throws IOException {
        resources = settings.getResources();

        if (resources == null) {
            throw new IllegalStateException("Cannot init fs resource loader while it's enabled");
        }
        resourcesListener = new FileUpdatesListener(resources);
    }

    @PreDestroy
    public void stopFSListeners() {
        resourcesListener.stopNear();
    }

    @Override
    public boolean hasResource(String url) {
        return resources != null && new File(resources, url).isFile();
    }

    @Override
    public InputStream getResource(String url) {
        if (hasResource(url)) {
            try {
                return new FileInputStream(new File(resources, url));
            } catch (IOException e) {
                throw new IllegalStateException("Cannot load file", e);
            }
        }
        return null;
    }

    @Override
    public Collection<String> getFontsUrls() {
        if (resources != null) {
            File[] fontsFiles = resources.listFiles((dir, fileName) -> IS_FONT.test(fileName));
            if (fontsFiles != null) {
                return Arrays.stream(fontsFiles)
                             .map(File::getAbsolutePath)
                             .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void registerListener(TemplateListener listener) {
        resourcesListener.registerListener(listener);
    }

    @Override
    public int order() {
        return ORDER_FS;
    }
}
