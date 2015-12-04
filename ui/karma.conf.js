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

'use strict';

var webpack = require('webpack'),
    webpackConfig = require('./webpack.config');

webpackConfig.plugins.push(new webpack.HotModuleReplacementPlugin());
webpackConfig.devtool = 'inline-source-map';

module.exports = function (config) {
    config.set({
        files: ['app/spec-runner.js'],
        frameworks: ['mocha'],
        browsers: ['Chrome'],
        reporters: ['mocha'],
        webpack: webpackConfig,
        preprocessors: {
            'app/spec-runner.js': ['webpack', 'sourcemap']
        },
        logLevel: config.LOG_ERROR,
        background: true,
        singleRun: false,
        plugins: [
            require('karma-sourcemap-loader'),
            require('karma-webpack'),
            require('karma-mocha'),
            require('karma-mocha-reporter'),
            require('karma-chrome-launcher')
        ]
    });
};
