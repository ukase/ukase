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

package me.entresol.ukase.service;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import me.entresol.ukase.web.UkasePayload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class HtmlRenderer {
    public String render(UkasePayload.HtmlTemplateInfo htmlTemplateInfo, Map<String, Object> params) throws IOException {
        Handlebars handlebars = new Handlebars();
        Template template;
        if (htmlTemplateInfo.getLocation() != null) {
            throw new UnsupportedOperationException("Not yet implemented!");
        } else {
            template = handlebars.compileInline(htmlTemplateInfo.getContent());
        }

        return template.apply(params);
    }
}
