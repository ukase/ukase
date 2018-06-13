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

package com.github.ukase.toolkit.upload;

import com.github.jknack.handlebars.io.AbstractTemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;
import com.github.ukase.toolkit.TemplateListenable;
import com.github.ukase.toolkit.TemplateListener;
import com.github.ukase.toolkit.UkaseTemplateLoader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@ConditionalOnProperty(name = {"ukase.enabled-sources.upload"}, havingValue = "true")
public class UploadedTemplateLoader extends AbstractTemplateLoader implements TemplateListenable, UkaseTemplateLoader {
    private static final String TEMPLATE_POSTFIX = ".hbs";
    private final Map<String, UploadedTemplateSource> templatesMap;
    private final Queue<TemplateListener> listeners = new ConcurrentLinkedQueue<>();

    public UploadedTemplateLoader() {
        this.templatesMap = new ConcurrentHashMap<>();
    }

    @Override
    public TemplateSource sourceAt(String location) throws IOException {
        return templatesMap.get(location + TEMPLATE_POSTFIX);
    }

    public void uploadTemplate(String resourceName, String resource) {
        UploadedTemplateSource template = new UploadedTemplateSource(resource, resourceName);
        if (!resourceName.endsWith(TEMPLATE_POSTFIX)) {
            resourceName += TEMPLATE_POSTFIX;
        }
        templatesMap.put(UploadSource.UPLOADED_RESOURCES_PREFIX + resourceName, template);

        notifyListeners(resourceName);
    }

    @Override
    public void registerListener(TemplateListener listener) {
        listeners.add(listener);
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
        return ORDER_UPLOAD;
    }

    private void notifyListeners(String resourceName) {
        TemplateListener listener;
        while ((listener = listeners.poll()) != null) {
            listener.resourceUpdated(resourceName);
        }
    }
}
