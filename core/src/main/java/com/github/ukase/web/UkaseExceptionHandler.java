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

package com.github.ukase.web;

import com.github.jknack.handlebars.HandlebarsException;
import com.github.ukase.toolkit.RenderException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class UkaseExceptionHandler {
    @ExceptionHandler(IOException.class)
    @ResponseBody
    public ResponseEntity<ExceptionMessage> handleIOException(IOException e, WebRequest request) {
        logRequest((RequestData) request.getAttribute(RequestData.ATTRIBUTE_NAME, RequestAttributes.SCOPE_REQUEST));

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ExceptionMessage message = new ExceptionMessage(e.getMessage(), status.value());
        log.warn("\nIO Exception: {}", e);
        return new ResponseEntity<>(message, status);
    }

    @ExceptionHandler(InterruptedException.class)
    @ResponseBody
    public ResponseEntity<ExceptionMessage> handleInterruptedException(InterruptedException e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ExceptionMessage message = new ExceptionMessage(e.getMessage(), status.value());
        log.warn("\nInterrupted: {}", message);
        return new ResponseEntity<>(message, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<List<ValidationError>> handleValidationException(MethodArgumentNotValidException e,
                                                                           WebRequest request) {
        logRequest((RequestData) request.getAttribute(RequestData.ATTRIBUTE_NAME, RequestAttributes.SCOPE_REQUEST));

        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        List<ValidationError> mappedErrors = allErrors.stream().map(ValidationError::new).collect(Collectors.toList());
        log.warn("\nValidation errors: {}", mappedErrors);
        return new ResponseEntity<>(mappedErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlebarsException.class)
    @ResponseBody
    public ResponseEntity<String> handleHandlebarsException(HandlebarsException e, WebRequest request) {
        logRequest((RequestData) request.getAttribute(RequestData.ATTRIBUTE_NAME, RequestAttributes.SCOPE_REQUEST));

        log.error("\nSome grand error caused in template mechanism", e);
        return new ResponseEntity<>("Some error caused in template mechanism", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RenderException.class)
    @ResponseBody
    public ResponseEntity<String> handleRenderException(RenderException e, WebRequest request) {
        logRequest((RequestData) request.getAttribute(RequestData.ATTRIBUTE_NAME, RequestAttributes.SCOPE_REQUEST));

        log.warn("\nRendering: " + e.getMessage(), e.getCause());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler()
    @ResponseBody
    public ResponseEntity<String> handleException(Exception e) {
        log.warn("\nCommon rendering problem: {}", e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }



    private void logRequest(RequestData data) {
        if (data == null) {
            log.warn("Failed load request data");
        } else if (data.getData() != null) {
            log.warn("\nFailed {} request {} with data\n{}\n", data.getMethod(), data.getFullUri(), data.getJsonData());
        } else {
            log.warn("\nFailed {} request {}.\n", data.getMethod(), data.getFullUri());
        }
    }

    @Data
    private static class ExceptionMessage {
        private final String message;
        private final int code;

        ExceptionMessage(String message, int code) {
            this.message = message;
            this.code = code;
        }
    }
}
