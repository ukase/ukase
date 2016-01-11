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

package com.github.ukase.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Component
@Data
@ConfigurationProperties(prefix = "ukase")
public class UkaseSettings {
    private static final String CLASSPATH = "classpath:/";

    private String projectRoot;
    private File templates;
    private File resources;
    private File jar;

    public void setTemplates(String path) {
        this.templates = translateToFile(path, true);
    }

    public void setResources(String path) {
        this.resources = translateToFile(path, true);
    }

    public void setJar(String path) {
        this.jar = translateToFile(path, false);
    }

    private File translateToFile(String path, boolean isDirectory) {
        File directory;

        if (path == null || path.trim().isEmpty()) {
            return null;
        }

        if (path.startsWith(CLASSPATH)) {
            if (path.length() == CLASSPATH.length()) {
                directory = new File(getClassPathUri());
            } else {
                directory = new File(new File(getClassPathUri()), path.substring(CLASSPATH.length()));
            }
        } else {
            directory = new File(path);
        }

        if (isDirectory) {
            if (!directory.isDirectory()) {
                throw new IllegalStateException("Wrong configuration - not a directory/file: " + directory);
            }
        } else if(!directory.isFile()) {
            throw new IllegalStateException("Wrong configuration - not a jar file: " + directory);
        }
        return directory;
    }

    private URI getClassPathUri() {
        try {
            URL classPath = getClass().getClassLoader().getResource(".");
            if (classPath == null) {
                throw new IllegalStateException("ClassPath is null");
            }
            return classPath.toURI();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Cannot resolve ClassPath");
        }
    }
}
