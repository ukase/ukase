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

package com.github.ukase.toolkit;

import com.github.jknack.handlebars.io.AbstractTemplateLoader;
import com.github.jknack.handlebars.io.StringTemplateSource;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CompoundTemplateLoader extends AbstractTemplateLoader {
    private static final String IMAGE_AS_PAGE = "default - image as page";
    private static final TemplateSource IMAGE_AS_PAGE_TEMPLATE;

    static {
        IMAGE_AS_PAGE_TEMPLATE =
                new StringTemplateSource(IMAGE_AS_PAGE, StaticUtils.readStringFile(getStream()));
    }

    private static InputStream getStream() {
        return CompoundTemplateLoader.class.getResourceAsStream("image-as-page.hbs");
    }

    private final List<TemplateLoader> templateLoaders;

    public CompoundTemplateLoader(List<UkaseTemplateLoader> loaders) {
        templateLoaders = map(loaders);
    }

    @Override
    public TemplateSource sourceAt(String location) throws IOException {
        if (IMAGE_AS_PAGE.equals(location)) {
            //TODO extract loader for default templates (if their count exceed 1)
            return IMAGE_AS_PAGE_TEMPLATE;
        }

        for(TemplateLoader loader: templateLoaders) {
            try  {
                return loader.sourceAt(location);
            } catch (IOException e) {
                //ignore
            }
        }

        String msg = "cannot load " + location + " in any available template loader";
        log.warn(msg);
        throw new IOException(msg);
    }

    private List<TemplateLoader> map(List<UkaseTemplateLoader> loaders) {
        List<UkaseTemplateLoader> sortedLoaders = new ArrayList<>(loaders);
        sortedLoaders.sort(new LoaderComparator());

        return sortedLoaders.stream()
                .filter(TemplateLoader.class::isInstance)
                .map(TemplateLoader.class::cast)
                .collect(Collectors.toList());
    }

    private static class LoaderComparator implements Comparator<UkaseTemplateLoader> {
        @Override
        public int compare(UkaseTemplateLoader o1, UkaseTemplateLoader o2) {
            return o1.order() - o2.order();
        }
    }
}
