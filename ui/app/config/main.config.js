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

var angular = require('angular');
require('ngStorage');

module.exports = angular
    .module('ukase.config.main', [
        require('angular-ui-router'),
        require('../routes/errors/errors.route'),
        'ngStorage'
    ])
    .config(mainConfig)
    .run(mainRun)
    .name;


function mainConfig($locationProvider, $urlRouterProvider, $httpProvider, $compileProvider, $rootScopeProvider) {
    $locationProvider.html5Mode(true);
    $httpProvider.useApplyAsync(true);
    $compileProvider.debugInfoEnabled(false);
    $rootScopeProvider.digestTtl(8);

    $urlRouterProvider.rule(function ($injector, $location) {
        var path = $location.path();

        if (path !== '/' && path.slice(-1) === '/') {
            $location.replace().path(path.slice(0, -1));
        }
    });

    $urlRouterProvider.otherwise('/404');
}

function mainRun($rootScope, $log) {
    var unRegisterFn = $rootScope.$on('$stateChangeError', $stateChangeError);
    $rootScope.$on('$destroy', unRegisterFn);

    function $stateChangeError(event, toState, toParams, fromState, fromParams, error) {
        /* eslint-disable no-console */
        console.group();
        $log.error('$stateChangeError', error);
        $log.error(error.stack);
        $log.info('event', event);
        $log.info('toState', toState);
        $log.info('toParams', toParams);
        $log.info('fromState', fromState);
        $log.info('fromParams', fromParams);
        console.groupEnd();
        /* eslint-enable no-console: 2 */
    }
}
