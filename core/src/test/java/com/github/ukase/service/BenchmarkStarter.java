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

import org.junit.Ignore;
import org.junit.Test;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

@Ignore("For manual start only")
public class BenchmarkStarter {
    @Test
    public void test512Html() throws RunnerException {
        runTest("512", HtmlRenderPerformanceTest.class);
    }
    @Test
    public void test512Pdf() throws RunnerException {
        runTest("512", PdfRenderPerformanceTest.class);
    }
    @Test
    public void test1024Html() throws RunnerException {
        runTest("1024", HtmlRenderPerformanceTest.class);
    }
    @Test
    public void test1024Pdf() throws RunnerException {
        runTest("1024", PdfRenderPerformanceTest.class);
    }
    @Test
    public void test1536Html() throws RunnerException {
        runTest("1536", HtmlRenderPerformanceTest.class);
    }
    @Test
    public void test1536Pdf() throws RunnerException {
        runTest("1536", PdfRenderPerformanceTest.class);
    }
    @Test
    public void test2048Html() throws RunnerException {
        runTest("2048", HtmlRenderPerformanceTest.class);
    }
    @Test
    public void test2048Pdf() throws RunnerException {
        runTest("2048", PdfRenderPerformanceTest.class);
    }

    private void runTest(String heapSize, Class<?> clazz) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(clazz.getName())
                // Set the following options as needed
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MILLISECONDS)
                .warmupTime(TimeValue.seconds(1))
                .warmupIterations(2)
                .measurementTime(TimeValue.seconds(1))
                .measurementIterations(1)
                .jvmArgsAppend("-Xmx" + heapSize + "m")
                .jvmArgsAppend("-Xms" + heapSize + "m")
                .threads(1)
                .addProfiler(GCProfiler.class)
                .forks(1)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                .build();

        new Runner(opt).run();
    }
}
