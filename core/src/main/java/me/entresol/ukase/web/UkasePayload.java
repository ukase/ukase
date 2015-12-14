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

package me.entresol.ukase.web;

import lombok.Data;
import me.entresol.ukase.web.validation.HtmlTemplateLocationExists;
import me.entresol.ukase.web.validation.RequiredAnyOf;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class UkasePayload {
    @NotNull
    @Valid
    private HtmlTemplateInfo index;
    private List<HtmlTemplateInfo> partials = new ArrayList<>();
    private Map<String, Object> data = new HashMap<>();

    @Data
    @RequiredAnyOf({"content", "location"})
    public static class HtmlTemplateInfo {
        private String name;
        private String content;
        @HtmlTemplateLocationExists
        private String location;
    }
}
