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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;

@Data
class RequestData {
    static final String ATTRIBUTE_NAME = "RequestData_attr";
    private static final ObjectWriter WRITER = new ObjectMapper().writer();

    private String method;
    private String fullUri;
    private Object data;

    RequestData(HttpServletRequest request, Object data) {
        String requestAddress = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null && queryString.length() > 0) {
            requestAddress += "?" + queryString;
        }

        fullUri = requestAddress;
        method = request.getMethod();
        this.data = data;
    }

    String getJsonData() {
        try {
            return WRITER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            return "<CANNOT BE MAPPED: " + e.getMessage() + ">";
        }
    }
}
