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

package me.entresol.ukase.toolkit.fs;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import lombok.Getter;
import me.entresol.ukase.config.UkaseSettings;
import me.entresol.ukase.toolkit.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
public class FileSource implements Source {

    @Getter
    private final TemplateLoader templateLoader;
    private final File resources;
    private final File templates;
    private final Collection<String> fonts;
    //TODO WATCH SERVICE

    @Autowired
    public FileSource(UkaseSettings settings) {
        templates = settings.getTemplates();
        templateLoader = new FileTemplateLoader(templates);
        resources = settings.getResources();

        File[] fontsFiles = resources.listFiles((dir, fileName) -> IS_FONT.test(fileName));
        fonts = Arrays.stream(fontsFiles)
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Helper<?>> getHelpers() {
        return Collections.emptyMap();
    }

    @Override
    public boolean hasHelpers() {
        return false;
    }

    @Override
    public boolean hasResource(String url) {
        return new File(resources, url).isFile();
    }

    @Override
    public boolean hasTemplate(String name) {
        return new File(templates, name + ".hbs").isFile();
    }

    @Override
    public InputStream getResource(String url) throws IOException {
        return new FileInputStream(new File(resources, url));
    }

    public Collection<String> getFontsUrls() {
        return Collections.unmodifiableCollection(fonts);
    }
}
