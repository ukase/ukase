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

package com.github.ukase.web;

import com.github.jknack.handlebars.HandlebarsException;
import com.github.ukase.bulk.BulkStatus;
import com.github.ukase.config.BulkConfig;
import com.github.ukase.service.BulkRenderer;
import com.github.ukase.service.HtmlRenderer;
import com.github.ukase.toolkit.CompoundSource;
import com.github.ukase.toolkit.RenderTaskBuilder;
import com.github.ukase.toolkit.SourceListener;
import com.github.ukase.toolkit.StaticUtils;
import com.itextpdf.text.DocumentException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
class UkaseController {
    private static final String BULK_RESPONSE_READY;
    private static final String BULK_RESPONSE_PROCESSING;
    private static final String BULK_RESPONSE_ERROR;

    static {
        BULK_RESPONSE_READY = StaticUtils.readStringFile(getStream("bulk-response-ready.json"));
        BULK_RESPONSE_PROCESSING = StaticUtils.readStringFile(getStream("bulk-response-processing.json"));
        BULK_RESPONSE_ERROR = StaticUtils.readStringFile(getStream("bulk-response-error.json"));
    }

    private static InputStream getStream(String fileName) {
        return UkaseController.class.getResourceAsStream(fileName);
    }

    @Autowired
    private HtmlRenderer htmlRenderer;
    @Autowired
    private CompoundSource source;
    @Autowired
    private BulkRenderer bulkRenderer;
    @Autowired
    private BulkConfig bulkConfig;
    @Autowired
    private RenderTaskBuilder taskBuilder;

    /*================================================================================================================
     ==============================================   API controllers   ==============================================
     =================================================================================================================*/

    @RequestMapping(value = "/html", method = RequestMethod.POST)
    public ResponseEntity<String> generateHtml(@RequestBody @Valid UkasePayload payload) throws IOException {
        String result = htmlRenderer.render(payload.getIndex(), payload.getData());
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/pdf", method = RequestMethod.POST, produces = "application/pdf")
    public ResponseEntity<byte[]> generatePdf(@RequestBody @Valid UkasePayload payload)
            throws IOException, DocumentException, URISyntaxException {
        return ResponseEntity.ok(taskBuilder.build(payload).call());
    }

    @RequestMapping(value = "/pdf/{template}", method = RequestMethod.HEAD)
    public @ResponseBody DeferredState checkTemplate(@PathVariable String template) throws IOException {
        DeferredState state = new DeferredState();
        SourceListener listener = SourceListener.templateListener(template,
                test -> state.setResult(translateState(test)));
        source.registerListener(listener);
        return state;
    }

    /*================================================================================================================
     ============================================== BulkAPI controllers ==============================================
     =================================================================================================================*/

    @RequestMapping(value = "/bulk", method = RequestMethod.POST,
            consumes = {"text/json", "text/json;charset=UTF-8", "application/json"})
    public @ResponseBody String postBulkInOrder(@RequestBody List<UkasePayload> payloads) throws IOException {
        return bulkRenderer.putTaskInOrder(payloads);
    }

    @RequestMapping(value = "/bulk/sync", method = RequestMethod.POST,
            produces = "application/pdf", consumes = "text/json")
    public ResponseEntity<byte[]> renderBulk(@RequestBody List<UkasePayload> payloads)
            throws IOException, InterruptedException {
        return ResponseEntity.ok(bulkRenderer.processOrder(payloads));
    }

    @RequestMapping(value = "/bulk/{uuid}", method = RequestMethod.GET, produces = "application/pdf")
    public ResponseEntity<byte[]> getBulk(@PathVariable String uuid) throws IOException {
        byte[] bulk = bulkRenderer.getOrder(uuid);
        if (bulk == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        return ResponseEntity.ok(bulk);
    }

    @RequestMapping(value = "/bulk/{uuid}", method = RequestMethod.HEAD)
    public ResponseEntity<String> getBulkState(@PathVariable String uuid) throws IOException {
        BulkStatus status = bulkRenderer.checkStatus(uuid);
        switch (status) {
            case PROCESSED:
                return ResponseEntity.status(bulkConfig.statusProcessed()).body(BULK_RESPONSE_READY);
            case ORDERED:
                return ResponseEntity.status(bulkConfig.statusOrdered()).body(BULK_RESPONSE_PROCESSING);
        }
        return ResponseEntity.status(bulkConfig.statusError()).body(BULK_RESPONSE_ERROR);
    }

    /*================================================================================================================
     ============================================== Exceptions handling ==============================================
     =================================================================================================================*/

    @ExceptionHandler(IOException.class)
    @ResponseBody
    public ResponseEntity<ExceptionMessage> handleIOException(IOException e) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ExceptionMessage message = new ExceptionMessage(e.getMessage(), status.value());
        return new ResponseEntity<>(message, status);
    }

    @ExceptionHandler(InterruptedException.class)
    @ResponseBody
    public ResponseEntity<ExceptionMessage> handleInterruptedException(InterruptedException e) {
        HttpStatus status = HttpStatus.GONE;
        ExceptionMessage message = new ExceptionMessage(e.getMessage(), status.value());
        return new ResponseEntity<>(message, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<List<ValidationError>> handleValidationException(MethodArgumentNotValidException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        List<ValidationError> mappedErrors = allErrors.stream().map(ValidationError::new).collect(Collectors.toList());
        return new ResponseEntity<>(mappedErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlebarsException.class)
    @ResponseBody
    public ResponseEntity<String> handleHandlebarsException(HandlebarsException e) {
        log.error("Some grand error caused in template mechanism", e);
        return new ResponseEntity<>("Some grand error caused in template mechanism", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /*================================================================================================================
     ==============================================  private utilities  ==============================================
     =================================================================================================================*/

    private ResponseEntity<Object> translateState(boolean selectedTemplateUpdated) {
        if (selectedTemplateUpdated) {
            return new ResponseEntity<>("updated", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
    }

    @Data
    private static class ExceptionMessage {
        private final String message;
        private final int code;

        public ExceptionMessage(String message, int code) {
            this.message = message;
            this.code = code;
        }
    }
}
