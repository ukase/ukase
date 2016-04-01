/*
 * Copyright (c) 2016 Konstantin Lepa <konstantin+ukase@lepabox.net>
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
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Data
@ConfigurationProperties(prefix = "ukase.bulk")
public class BulkConfig {
    private int threads;
    private boolean statusCodes;
    private Long ttl;
    private File path;

    private HttpStatus processed = HttpStatus.OK;
    private HttpStatus ordered = HttpStatus.PROCESSING;
    private HttpStatus error = HttpStatus.BAD_REQUEST;

    @Bean
    public ExecutorService provideExecutor() {
        return Executors.newFixedThreadPool(threads);
    }

    @Bean
    public File getPath() {
        return path;
    }

    @Bean
    public Long getTtl() {
        return ttl;
    }

    public void setPath(File path) throws Exception {
        if (path == null || path.isFile()) {
            throw new Exception("Bulk storage not configured well");
        }
        if (!path.isDirectory() && (!path.mkdirs())) {
            throw new Exception("Bulk storage is not directory or cannot be created");
        }
        this.path = path;
    }

    public void setStatusCodes(boolean codes) {
        statusCodes = codes;
        if (!statusCodes) {
            processed = ordered = error = HttpStatus.OK;
        }
    }

    /**
     * Set TTL (time to live) for rendered pdf bulks
     * @param ttl value in minutes
     */
    public void setTtl(long ttl) {
        this.ttl = ttl * 60 // minutes -> seconds
                       * 1000; // seconds -> milliseconds
    }

    public HttpStatus statusProcessed() {
        return processed;
    }

    public HttpStatus statusOrdered() {
        return ordered;
    }

    public HttpStatus statusError() {
        return error;
    }
}
