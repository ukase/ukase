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

package com.github.ukase.service;

import com.github.ukase.config.UkaseSettings;
import com.github.ukase.toolkit.CompoundSource;
import com.github.ukase.toolkit.CompoundTemplateLoader;
import com.github.ukase.toolkit.ResourceProvider;
import com.github.ukase.toolkit.fs.FSTemplateLoader;
import com.github.ukase.toolkit.fs.FileSource;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class AbstractNoSpringBenchmark {
    @Getter
    private final ResourceProvider resourceProvider;

    AbstractNoSpringBenchmark() {
        ResourceProvider resourceProvider = null;
        try {
            ApplicationContext context = new NoSpringApplicationContext();
            UkaseSettings settings = new UkaseSettings();
            settings.setTemplates("target/test-classes/");
            settings.setResources("target/test-classes/");

            FSTemplateLoader fsTemplateLoader = new FSTemplateLoader(settings);
            FileSource fileSource = new FileSource(settings);

            CompoundTemplateLoader templateLoader = new CompoundTemplateLoader(Collections.singletonList(fsTemplateLoader));

            CompoundSource compoundSource = new CompoundSource(Collections.singletonList(fileSource));
            resourceProvider = new ResourceProvider(context, compoundSource, templateLoader, settings);
        } catch (IOException e) {
            //ignore
        }
        this.resourceProvider = resourceProvider;
    }


    private static class NoSpringApplicationContext extends AbstractApplicationContext {
        @Override
        protected void refreshBeanFactory() throws BeansException, IllegalStateException {/*do nothing*/}
        @Override
        protected void closeBeanFactory() {/*do nothing*/}
        @Override
        public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {return null;}

        @Override
        public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
            return Collections.emptyMap();
        }
    }

    static Map<String, Object> prepareData(int count, int columns) {
        Map<String, Object> data = new HashMap<>();
        data.put("index0", "паывапцуйукапйуцкрпй");
        data.put("index1", "ппрйупйуцкп");
        data.put("index2", "gqwrgqwerhqerg");
        data.put("index3", "gewrhgwerhwerheруццукрцук пуцкп уцкп уцкп ");
        List<Map<String, Object>> array = new ArrayList<>();
        data.put("array", array);

        for(int i = 0 ; i < count ; i++) {
            Map<String, Object> subData = new HashMap<>();
            array.add(subData);

            for(int z = 1 ; z < columns ; z++) {
                subData.put("f" + z, z + "аыфваф" + i);
            }
        }
        return data;
    }
}
