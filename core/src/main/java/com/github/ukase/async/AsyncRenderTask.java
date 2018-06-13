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
import lombok.extern.log4j.Log4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Log4j
class AsyncRenderTask implements AsyncTask {
    private final SingleRenderTask subTask;
    private Future<byte[]> futureTask;
    private final AsyncRequest request;

    AsyncRenderTask(RenderTask task, AsyncRequest request) {
        this.request = request;
        subTask = new SingleRenderTask(task, this::childResult);
    }

    @Override
    public String getId() {
        return request.getUuid();
    }

    @Override
    public byte[] getResult() throws InterruptedException {
        try {
            return futureTask.get();
        } catch (ExecutionException e) {
            throw new RenderException("Async render failed", e, "async task");
        }
    }

    @Override
    public synchronized AsyncRenderTask startOnExecutor(ExecutorService service) {
        if (futureTask != null) {
            return this;
        }
        futureTask = service.submit(subTask);
        return this;
    }

    private void childResult(Boolean renderResult) {
        if (!renderResult) {
            request.error();
        } else {
            request.saveData(subTask.getData());
        }
    }
}
