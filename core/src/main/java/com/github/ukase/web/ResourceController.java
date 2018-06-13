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

package com.github.ukase.web;

import com.github.ukase.toolkit.upload.UploadSource;
import com.github.ukase.toolkit.upload.UploadedTemplateLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/api/resources")
@ConditionalOnProperty(name = {"ukase.enabled-sources.upload"}, havingValue = "true")
public class ResourceController {
    private final UploadedTemplateLoader templatesLoader;
    private final UploadSource resourcesLoader;

    @Autowired
    public ResourceController(UploadedTemplateLoader templatesLoader, UploadSource resourcesLoader) {
        this.templatesLoader = templatesLoader;
        this.resourcesLoader = resourcesLoader;
    }

    @RequestMapping("/upload/{name}")
    public ResponseEntity<?> uploadResource(@RequestBody String resource, @PathVariable("name") String resourceName) {
        templatesLoader.uploadTemplate(resourceName, resource);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/upload/",
            consumes = {
                    MimeTypeUtils.TEXT_PLAIN_VALUE,
                    MimeTypeUtils.TEXT_PLAIN_VALUE + ";charset=UTF-8",
                    MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE
            }
    )
    public ResponseEntity<?> uploadResourcePath(@RequestBody byte[] resource,
                                                @RequestHeader("path") String resourcePath,
                                                @RequestHeader(value = "isResource", required = false) Boolean isResource,
                                                @RequestHeader(value = "charset", required = false) String charset) {
        if (isResource != null && isResource) {
            resourcesLoader.uploadResource(resourcePath, resource);
        } else {
            Charset templatesCharset = charset == null
                    ? StandardCharsets.UTF_8
                    : Charset.forName(charset);
            templatesLoader.uploadTemplate(resourcePath, new String(resource, templatesCharset));
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
