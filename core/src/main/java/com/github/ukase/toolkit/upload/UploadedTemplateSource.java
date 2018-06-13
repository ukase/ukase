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

import com.github.jknack.handlebars.io.TemplateSource;

public class UploadedTemplateSource implements TemplateSource {
    private final String content;
    private final String name;
    private final long lastModified;

    UploadedTemplateSource(String content, String name) {
        this.content = content;
        this.name = name;
        this.lastModified = System.currentTimeMillis();
    }

    @Override
    public String content() {
        return content;
    }

    @Override
    public String filename() {
        return name;
    }

    @Override
    public long lastModified() {
        return lastModified;
    }
}