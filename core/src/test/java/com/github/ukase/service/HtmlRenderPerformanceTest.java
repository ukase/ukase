/*
 * Copyright (c) 2017 Konstantin Lepa <konstantin+ukase@lepabox.net>
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

import com.github.ukase.web.UkasePayload;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@State(Scope.Thread)
public class HtmlRenderPerformanceTest {
    public static HtmlRenderer htmlRenderer;

    Map<String, Object> data;

    @Setup(Level.Trial)
    public void initialize() {
        data = new HashMap<>();
        data.put("index0", "паывапцуйукапйуцкрпй");
        data.put("index1", "ппрйупйуцкп");
        data.put("index2", "gqwrgqwerhqerg");
        data.put("index3", "gewrhgwerhwerheруццукрцук пуцкп уцкп уцкп ");
        List<Map<String, Object>> array = new ArrayList<>();
        data.put("array", array);

        for(int i = 0 ; i < 1_000 ; i++) {
            Map<String, Object> subData = new HashMap<>();
            array.add(subData);

            for(int z = 1 ; z < 84 ; z++) {
                subData.put("f" + z, z + "аыфваф" + i + " asdfas" + z);
            }
        }
    }

    @Benchmark
    public void html(Blackhole blackhole) {
        UkasePayload data = new UkasePayload();
        data.setData(this.data);
        data.setIndex("performance.pdf");
        blackhole.consume(htmlRenderer.render(data));
    }
}
