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

var config,
    path = require('path'),
    webpack = require('webpack'),
    AppCachePlugin = require('appcache-webpack-plugin'),
    HtmlWebpackPlugin = require('html-webpack-plugin'),
    ExtractTextPlugin = require("extract-text-webpack-plugin"),
    TARGET = process.env.npm_lifecycle_event,
    APP_CACHE_FILE = 'cache.appcache';

config = {
    context: __dirname,
    entry: path.resolve(__dirname, 'app', 'main.js'),
    output: {
        path: path.resolve(__dirname, 'target', 'dist'),
        filename: 'bundle.[hash].js',
        pathinfo: TARGET !== 'build'
    },
    resolve: {
        alias: {
            sinon: 'sinon/pkg/sinon.js' // https://github.com/webpack/webpack/issues/304
        }
    },
    module: {
        preLoaders: [
            {test: /\.json$/, loader: 'json'}
        ],
        loaders: [
            {test: /\.bpmn$/, loader: 'raw', exclude: /node_modules/},
            {test: /\.js$/, loader: 'ng-annotate!eslint', exclude: /node_modules/},
            {test: /index\.html$/, loader: 'file?name=[name].html', exclude: /node_modules/},
            {test: /\.html$/, loader: 'raw', exclude: /node_modules/},
            {test: /\.css$/, loader: ExtractTextPlugin.extract('style', 'css!autoprefixer?browsers=last 2 version'), exclude: /node_modules/},
            {test: /\.less$/, loader: ExtractTextPlugin.extract('style', 'css!less!autoprefixer?browsers=last 2 version'), exclude: /node_modules/},
            {test: /\.css$/, loader: ExtractTextPlugin.extract('style', 'css'), exclude: /app/},
            {test: /\.less$/, loader: ExtractTextPlugin.extract('style', 'css!less'), exclude: /app/},
            {test: /\.(png|woff(2)?|eot|ttf|svg)(\?[a-z0-9=\.]+)?$/, loader: 'file'},
            {test: /jquery.*\.js$/, loader: 'expose?jQuery'},
            {test: /angular.*\.js$/, loader: 'imports?jQuery=jquery,$=jquery,this=>window'},
            {test: /sinon.*\.js$/, loader: 'imports?define=>false,require=>false'}
        ],
        noParse: [/node_modules\/sinon\//]
    },
    plugins: [
        new ExtractTextPlugin('bundle.[hash].css'),
        new HtmlWebpackPlugin({
            inject: 'body',
            template: path.resolve(__dirname, 'app', 'index.html'),
            appCacheManifest: TARGET === 'build' ? APP_CACHE_FILE : ''
        })
    ],
    devtool: 'eval',
    devServer: {
        port: 8888,
        historyApiFallback: true,
        proxy: {
            '/api/*': 'http://localhost:10080/'
        }
    }
};

if (TARGET === 'build') {
    config.devtool = 'source-map';
    config.plugins.push(
        new AppCachePlugin({
            settings: ['prefer-online'],
            output: 'cache.appcache'
        }));

    config.plugins.push(new webpack.optimize.UglifyJsPlugin());
}

module.exports = config;
