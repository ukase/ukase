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

package com.github.ukase.async;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Data
@Slf4j
class AsyncRequest {
    private AsyncStatus status;
    private String uuid;
    private Long buildTime;
    private File dataFile;

    AsyncRequest() {
        this.status = AsyncStatus.ORDERED;
    }

    AsyncRequest(File dataFile, String uuid, Long buildTime) {
        this.status = AsyncStatus.PROCESSED;
        this.dataFile = dataFile;
        this.uuid = uuid;
        this.buildTime = buildTime;
    }

    void saveData(byte[] data) {
        File parent = dataFile.getParentFile();
        if (!parent.exists()) {
            if (!parent.mkdir()) {
                error();
            }
        } else if (!parent.isDirectory()) {
            error();
        } else {
            try (FileOutputStream fos = new FileOutputStream(dataFile, false)) {
                fos.write(data);
                fos.flush();
                success();
            } catch (IOException e) {
                log.error("Cannot create pdf file " + dataFile.getName(), e);
                error();
            }
        }
    }

    void error() {
        status = AsyncStatus.ERROR;
        markTime();
    }

    private void success() {
        status = AsyncStatus.PROCESSED;
        markTime();
    }

    private void markTime() {
        buildTime = System.currentTimeMillis();
    }
}
