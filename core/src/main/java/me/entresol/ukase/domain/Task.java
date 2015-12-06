/*
 * Copyright (c) 2015 Konstantin Lepa <konstantin+ukase@lepabox.net>
 *
 * This file is part of Ukase.
 *
 * Ukase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package me.entresol.ukase.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.*;

@Data
public class Task {
    private String token;
    private Status status;
    @JsonIgnore
    private Payload payload;

    public Task(Status status) {
        this.status = status;
    }

    public Task(Payload payload) {
        this.payload = payload;
    }

    public enum Status {
        ERROR, DONE, IN_PROGRESS
    }

    @Data
    public static class Payload {
        private List<HtmlTemplate> htmlTemplates;
        private Map<String, Object> data;

        @Data
        public static class HtmlTemplate {
            private String location;
            private String name;
            private String content;
        }
    }
}
