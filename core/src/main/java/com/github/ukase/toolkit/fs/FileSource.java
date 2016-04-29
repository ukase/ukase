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

package com.github.ukase.toolkit.fs;

import com.github.jknack.handlebars.Helper;
import com.github.ukase.toolkit.SourceListener;
import com.github.ukase.config.UkaseSettings;
import com.github.ukase.toolkit.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FileSource implements Source {
    private final File resources;
    private final File templates;
    private final FileUpdatesListener resourcesListener;
    private final FileUpdatesListener templatesListener;
    private final Collection<String> fonts;

    @Autowired
    public FileSource(UkaseSettings settings) throws IOException {
        templates = settings.getTemplates();
        resources = settings.getResources();

        if (resources != null) {
            resourcesListener = new FileUpdatesListener(resources);
            if (isSubDirectory(resources, templates)) {
                templatesListener = null;
            } else {
                templatesListener = new FileUpdatesListener(templates);
            }

            File[] fontsFiles = resources.listFiles((dir, fileName) -> IS_FONT.test(fileName));
            fonts = Arrays.stream(fontsFiles)
                    .map(File::getAbsolutePath)
                    .collect(Collectors.toList());
        } else {
            resourcesListener = null;
            if (templates != null) {
                templatesListener = new FileUpdatesListener(templates);
            } else {
                templatesListener = null;
            }
            fonts = Collections.emptyList();
        }
    }

    @PreDestroy
    public void stopFSListeners() {
        if (resourcesListener != null) {
            resourcesListener.stopNear();
        }
        if (templatesListener != null) {
            templatesListener.stopNear();
        }
    }

    @Override
    public Map<String, Helper<?>> getHelpers() {
        return Collections.emptyMap();
    }

    @Override
    public boolean hasHelpers() {
        return false;
    }

    @Override
    public boolean hasResource(String url) {
        return resources != null && new File(resources, url).isFile();
    }

    @Override
    public boolean hasTemplate(String name) {
        return templates != null && new File(templates, name + ".hbs").isFile();
    }

    @Override
    public InputStream getResource(String url) throws IOException {
        if (hasResource(url)) {
            return new FileInputStream(new File(resources, url));
        }
        return null;
    }

    public Collection<String> getFontsUrls() {
        return Collections.unmodifiableCollection(fonts);
    }

    @Override
    public void registerListener(SourceListener listener) {
        if (resourcesListener != null) {
            resourcesListener.registerListener(listener);
        }
        if (templatesListener != null) {
            templatesListener.registerListener(listener);
        }
        if (resourcesListener == null && templatesListener == null) {
            listener.resourceUpdated(null);
        }
    }

    private boolean isSubDirectory(File dir, File kind) {
        while (kind != null) {
            if (kind.equals(dir)) {
                return true;
            }
            kind = kind.getParentFile();
        }
        return false;
    }
}
