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

package com.github.ukase;

import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public final class TestHelper {

    public static String getFileContent(String fileName, Class clazz) throws IOException {
        return getFileContent(fileName, clazz.getClassLoader());
    }

    private static String getFileContent(String fileName, ClassLoader loader) throws IOException {
        InputStream stream = loader.getResourceAsStream(fileName);
        return StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
    }

    private TestHelper() {
    }
}
