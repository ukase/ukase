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

import com.github.ukase.toolkit.Source;
import com.github.ukase.toolkit.TemplateListenable;
import com.github.ukase.toolkit.TemplateListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@ConditionalOnProperty(name = {"ukase.enabled-sources.upload"}, havingValue = "true")
public class UploadSource implements Source, TemplateListenable {
    static final String UPLOADED_RESOURCES_PREFIX = "upload://";
    private final Map<String, byte[]> uploadedResources;
    private final Queue<TemplateListener> listeners = new ConcurrentLinkedQueue<>();

    public UploadSource() {
        uploadedResources = new HashMap<>();
    }

    @Override
    public boolean hasResource(String url) {
        return url.startsWith(UPLOADED_RESOURCES_PREFIX)
                && uploadedResources.get(url) != null;
    }

    @Override
    public InputStream getResource(String url) {
        if (!url.startsWith(UPLOADED_RESOURCES_PREFIX)) {
            return null;
        }
        byte[] data = uploadedResources.get(url);
        if (data == null) {
            return null;
        }
        return new ByteArrayInputStream(data);
    }

    @Override
    public void registerListener(TemplateListener listener) {
        listeners.add(listener);
    }

    @Override
    public Collection<String> getFontsUrls() {
        return null;
    }

    @Override
    public int order() {
        return ORDER_UPLOAD;
    }

    public void uploadResource(String resourcePath, byte[] resource) {
        if (!resourcePath.startsWith(UPLOADED_RESOURCES_PREFIX)) {
            resourcePath = UPLOADED_RESOURCES_PREFIX + resourcePath;
        }
        uploadedResources.put(resourcePath, resource);

        notifyListeners(resourcePath);
    }

    private void notifyListeners(String resourceName) {
        TemplateListener listener;
        while ((listener = listeners.poll()) != null) {
            listener.resourceUpdated(resourceName);
        }
    }
}
