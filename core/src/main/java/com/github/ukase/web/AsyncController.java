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

import com.github.ukase.async.AsyncManager;
import com.github.ukase.async.AsyncStatus;
import com.github.ukase.config.BulkConfig;
import com.github.ukase.toolkit.StaticUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.InputStream;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/async")
public class AsyncController {
    private static final String BULK_RESPONSE_READY;
    private static final String BULK_RESPONSE_PROCESSING;
    private static final String BULK_RESPONSE_ERROR;
    static {
        BULK_RESPONSE_READY = StaticUtils.readStringFile(getStream("bulk-response-ready.json"));
        BULK_RESPONSE_PROCESSING = StaticUtils.readStringFile(getStream("bulk-response-processing.json"));
        BULK_RESPONSE_ERROR = StaticUtils.readStringFile(getStream("bulk-response-error.json"));
    }

    private final AsyncManager asyncManager;
    private final BulkConfig bulkConfig;

    @Autowired
    public AsyncController(AsyncManager asyncManager, BulkConfig bulkConfig) {
        this.asyncManager = asyncManager;
        this.bulkConfig = bulkConfig;
    }

    @RequestMapping(value = "/xlsx", method = RequestMethod.POST)
    public @ResponseBody String startRenderXlsx(@RequestBody @Valid UkasePayload payload) {
        return asyncManager.putXlsxTaskInOrder(payload);
    }

    @RequestMapping(value = "/pdf", method = RequestMethod.POST,
            consumes = {"text/json", "text/json;charset=UTF-8", "application/json"})
    public @ResponseBody String startRenderPdf(@RequestBody UkasePayload payload) {
        return asyncManager.putPdfTaskInOrder(payload);
    }

    @RequestMapping(value = "/pdf/bulk", method = RequestMethod.POST,
            consumes = {"text/json", "text/json;charset=UTF-8", "application/json"})
    public @ResponseBody String postBulkInOrder(@RequestBody List<UkasePayload> payloads) {
        return asyncManager.putBulkInOrder(payloads);
    }

    @RequestMapping(value = "/{uuid}", method = RequestMethod.GET, produces = "application/octet-stream")
    public ResponseEntity<byte[]> getBulk(@PathVariable String uuid) {
        byte[] bulk = asyncManager.getOrder(uuid);
        if (bulk == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.ok(bulk);
    }

    @RequestMapping(value = "/{uuid}/status", method = RequestMethod.GET)
    public ResponseEntity<String> getBulkState(@PathVariable String uuid) {
        AsyncStatus status = asyncManager.checkStatus(uuid);
        switch (status) {
            case PROCESSED:
                return ResponseEntity.status(bulkConfig.statusProcessed()).body(BULK_RESPONSE_READY);
            case ORDERED:
                return ResponseEntity.status(bulkConfig.statusOrdered()).body(BULK_RESPONSE_PROCESSING);
        }
        return ResponseEntity.status(bulkConfig.statusError()).body(BULK_RESPONSE_ERROR);
    }


    private static InputStream getStream(String fileName) {
        return UkaseController.class.getResourceAsStream(fileName);
    }
}
