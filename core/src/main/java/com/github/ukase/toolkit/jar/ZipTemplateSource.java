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

import com.github.jknack.handlebars.io.TemplateSource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipTemplateSource implements TemplateSource {
    private final ZipEntry entry;
    private final ZipFile zip;

    public ZipTemplateSource(ZipFile zip, ZipEntry entry) {
        this.entry = entry;
        this.zip = zip;
    }

    @Override
    public String content() throws IOException {
        return StreamUtils.copyToString(zip.getInputStream(entry), Charset.forName("UTF-8"));
    }

    @Override
    public String filename() {
        return entry.getName();
    }

    @Override
    public long lastModified() {
        return entry.getLastModifiedTime().toMillis();
    }
}
