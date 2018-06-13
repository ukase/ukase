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

import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.ukase.config.UkaseSettings;
import com.github.ukase.toolkit.TemplateListenable;
import com.github.ukase.toolkit.TemplateListener;
import com.github.ukase.toolkit.UkaseTemplateLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;

@Component
@ConditionalOnProperty(name = {"ukase.enabled-sources.fs"}, havingValue = "true")
public class FSTemplateLoader extends FileTemplateLoader implements TemplateListenable, UkaseTemplateLoader {
    private final FileUpdatesListener templatesListener;

    @Autowired
    public FSTemplateLoader(UkaseSettings settings) throws IOException {
        super(settings.getTemplates());

        File templates = settings.getTemplates();

        if (templates == null) {
            throw new IllegalStateException("Cannot init fs template loader while it is enabled");
        }
        templatesListener = new FileUpdatesListener(templates);
    }

    @PreDestroy
    public void stopFSListeners() {
        templatesListener.stopNear();
    }

    @Override
    public void registerListener(TemplateListener listener) {
        templatesListener.registerListener(listener);
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
        return ORDER_FS;
    }
}
