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

package com.github.ukase.toolkit;

import com.github.jknack.handlebars.Handlebars;
import com.github.ukase.config.UkaseSettings;
import com.github.ukase.toolkit.fs.FileSource;
import com.github.ukase.toolkit.helpers.AbstractHelper;
import com.github.ukase.toolkit.jar.JarSource;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.IOException;

@Service
public class ResourceProvider {
    private static final Logger log = LoggerFactory.getLogger(ResourceProvider.class);
    @Autowired
    private ApplicationContext context;
    private Source source;

    @Autowired
    public ResourceProvider(UkaseSettings settings, ApplicationContext context) {
        this.source = settings.isJar()
                ? context.getBean(JarSource.class)
                : context.getBean(FileSource.class);
    }

    @Bean
    public Handlebars getEngine() {
        Handlebars engine = new Handlebars(source.getTemplateLoader());

        if (source.hasHelpers()) {
            source.getHelpers().forEach(engine::registerHelper);
        }
        context.getBeansOfType(AbstractHelper.class).values().stream().forEach(
                helper -> engine.registerHelper(helper.getName(), helper)
        );

        return engine;
    }

    @Bean
    public ITextRenderer getRenderer() {
        ITextRenderer renderer = new ITextRenderer();
        SharedContext sharedContext = renderer.getSharedContext();
        sharedContext.setUserAgentCallback(new WrappedUserAgentCallback(source, sharedContext));

        ITextFontResolver resolver = renderer.getFontResolver();
        try {
            for (String font: source.getFontsUrls()) {
                resolver.addFont(font, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            }
        } catch (IOException | DocumentException e) {
            log.error("Some problem in font loading");
        }

        return renderer;
    }

    @Bean
    @Qualifier("calculated")
    public Source getSource() {
        return source;
    }
}
