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

import com.github.ukase.bulk.BulkStatus;
import com.github.ukase.config.BulkConfig;
import com.github.ukase.service.BulkRenderer;
import com.github.ukase.service.HtmlRenderer;
import com.github.ukase.service.XlsxRenderer;
import com.github.ukase.toolkit.CompoundSource;
import com.github.ukase.toolkit.RenderTaskBuilder;
import com.github.ukase.toolkit.SourceListener;
import com.github.ukase.toolkit.StaticUtils;
import com.itextpdf.text.DocumentException;
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
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

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
    private XlsxRenderer xlsxRenderer;
    @Autowired
    private RenderTaskBuilder taskBuilder;

    /*================================================================================================================
     ==============================================   API controllers   ==============================================
     =================================================================================================================*/

    @RequestMapping(value = "/html", method = RequestMethod.POST)
    public ResponseEntity<String> generateHtml(@RequestBody @Valid UkasePayload payload,
                                               HttpServletRequest request,
                                               WebRequest webRequest) throws IOException {
        webRequest.setAttribute(RequestData.ATTRIBUTE_NAME,
                new RequestData(request, payload),
                RequestAttributes.SCOPE_REQUEST);
        String result = htmlRenderer.render(payload.getIndex(), payload.getData());
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/pdf", method = RequestMethod.POST, produces = "application/pdf")
    public ResponseEntity<byte[]> generatePdf(@RequestBody @Valid UkasePayload payload,
                                              HttpServletRequest request,
                                              WebRequest webRequest)
            throws IOException, DocumentException, URISyntaxException {
        webRequest.setAttribute(RequestData.ATTRIBUTE_NAME,
                new RequestData(request, payload),
                RequestAttributes.SCOPE_REQUEST);
        log.debug("Generate PDF POST for '{}' :\n{}\n", payload.getIndex(), payload.getData());
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

    @RequestMapping(value = "/xlsx", method = RequestMethod.POST,
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> generateXlsx(@RequestBody @Valid UkasePayload payload,
                                               HttpServletRequest request,
                                               WebRequest webRequest) throws IOException {
        webRequest.setAttribute(RequestData.ATTRIBUTE_NAME,
                new RequestData(request, payload),
                RequestAttributes.SCOPE_REQUEST);
        log.debug("Generate XLSX POST for '{}' :\n{}\n", payload.getIndex(), payload.getData());
        String html = htmlRenderer.render(payload.getIndex(), payload.getData());
        log.debug("Prepared xhtml:\n{}\n", html);
        return ResponseEntity.ok(xlsxRenderer.render(html));
    }

    /*================================================================================================================
     ============================================== BulkAPI controllers ==============================================
     =================================================================================================================*/

    @RequestMapping(value = "/bulk", method = RequestMethod.POST,
            consumes = {"text/json", "text/json;charset=UTF-8", "application/json"})
    public @ResponseBody String postBulkInOrder(@RequestBody List<UkasePayload> payloads,
                                                HttpServletRequest request,
                                                WebRequest webRequest) throws IOException {
        webRequest.setAttribute(RequestData.ATTRIBUTE_NAME,
                new RequestData(request, payloads),
                RequestAttributes.SCOPE_REQUEST);
        return bulkRenderer.putTaskInOrder(payloads);
    }

    @RequestMapping(value = "/bulk/sync", method = RequestMethod.POST,
            produces = "application/pdf", consumes = "text/json")
    public ResponseEntity<byte[]> renderBulk(@RequestBody List<UkasePayload> payloads,
                                             HttpServletRequest request,
                                             WebRequest webRequest)
            throws IOException, InterruptedException {
        webRequest.setAttribute(RequestData.ATTRIBUTE_NAME,
                new RequestData(request, payloads),
                RequestAttributes.SCOPE_REQUEST);
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

    @RequestMapping(value = "/bulk/status/{uuid}", method = RequestMethod.GET)
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
     ==============================================  private utilities  ==============================================
     =================================================================================================================*/

    private ResponseEntity<Object> translateState(boolean selectedTemplateUpdated) {
        if (selectedTemplateUpdated) {
            return new ResponseEntity<>("updated", HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
    }
}
