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

package com.github.ukase.bulk;

import com.github.ukase.toolkit.RenderTask;
import com.itextpdf.text.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

class SingleRenderTask implements RenderTask {
    private static final Logger log = LoggerFactory.getLogger(SingleRenderTask.class);

    private final RenderTask task;
    private final BulkRenderTask parentTask;
    private byte[] pdf;

    SingleRenderTask(RenderTask task, BulkRenderTask parentTask) {
        this.task = task;
        this.parentTask = parentTask;
    }

    @Override
    public byte[] call() {
        try {
            pdf = task.call();
            if (parentTask != null) {
                parentTask.childProcessed();
            }
            return pdf;
        } catch (IOException | DocumentException | URISyntaxException e) {
            log.warn("Cannot produce pdf", e);
        }
        return pdf;
    }

    public byte[] getPdf() {
        return pdf;
    }

    @Override
    public String getTemplateName() {
        return task.getTemplateName();
    }
}
