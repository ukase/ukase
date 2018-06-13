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

package com.github.ukase.async;

import com.github.ukase.toolkit.render.RenderException;
import com.github.ukase.toolkit.render.RenderTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

class SingleRenderTask implements RenderTask {
    private static final Logger log = LoggerFactory.getLogger(SingleRenderTask.class);

    private final RenderTask task;
    private final Consumer<Boolean> resultConsumer;
    private byte[] data;

    SingleRenderTask(RenderTask task, Consumer<Boolean> resultConsumer) {
        this.task = task;
        this.resultConsumer = resultConsumer;
    }

    @Override
    public byte[] call() {
        try {
            data = task.call();
            if (resultConsumer != null) {
                resultConsumer.accept(true);
            }
            return data;
        } catch (RenderException e) {
            if (resultConsumer != null) {
                resultConsumer.accept(false);
            }
            log.warn("Cannot produce data", e);
        }
        return data;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String getTemplateName() {
        return task.getTemplateName();
    }
}
