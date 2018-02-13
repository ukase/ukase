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

package com.github.ukase.service;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.HandlebarsException;
import com.github.jknack.handlebars.Template;
import com.github.ukase.toolkit.render.RenderException;
import com.github.ukase.web.UkasePayload;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Log4j
public class HtmlRenderer implements Renderer<UkasePayload, String> {
    private Handlebars handlebars;

    @Autowired
    public HtmlRenderer(Handlebars handlebars) {
        this.handlebars = handlebars;
    }

    @Override
    public String render(UkasePayload data) throws RenderException {
        try {
            log.debug("Start rendering html, template" + data.getIndex());
            Template template = handlebars.compile(data.getIndex());
            log.debug("Template " + data.getIndex() + " compiled, rendering");
            String html = template.apply(data.getData());
            log.debug("Html rendered with size:" + html.length());
            return html;
        } catch (IOException|HandlebarsException|NullPointerException e) {
            throw new RenderException("Cannot produce html", e, "html");
        }
    }
}
