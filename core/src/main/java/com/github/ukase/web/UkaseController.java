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
import com.github.ukase.service.HtmlRenderer;
import com.github.ukase.service.PdfRenderer;
import com.github.ukase.toolkit.CompoundSource;
import com.github.ukase.toolkit.SourceListener;
import com.itextpdf.text.DocumentException;
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
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
public class UkaseController {
    @Autowired
    private HtmlRenderer htmlRenderer;
    @Autowired
    private PdfRenderer pdfRenderer;
    @Autowired
    private CompoundSource source;

    @RequestMapping(value = "/html", method = RequestMethod.POST)
    public ResponseEntity<String> generateHtml(@RequestBody @Valid UkasePayload payload) throws IOException {
        String result = htmlRenderer.render(payload.getIndex(), payload.getData());
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/pdf", method = RequestMethod.POST)
    public ResponseEntity<byte[]> generatePdf(@RequestBody @Valid UkasePayload payload)
            throws IOException, DocumentException, URISyntaxException {
        return ResponseEntity.ok(pdfRenderer.render(htmlRenderer.render(payload.getIndex(), payload.getData())));
    }

    @RequestMapping(value = "/pdf/{template}", method = RequestMethod.HEAD)
    public @ResponseBody DeferredState checkTemplate(@PathVariable String template) throws IOException {
        DeferredState state = new DeferredState();
        SourceListener listener = SourceListener.templateListener(template,
                test -> state.setResult(translateState(test)));
        source.registerListener(listener);
        return state;
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

    private ResponseEntity<Object> translateState(boolean selectedTemplateUpdated) {
        if (selectedTemplateUpdated) {
            return new ResponseEntity<>("updated", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
    }
}
