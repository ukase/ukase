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

'use strict';

var angular = require('angular'),
    ngModule;

ngModule = angular.module('ukase.components.ukase-data', [])
    .directive('ukaseData', ukaseData);

require('./ukase-data.controller')(ngModule);

module.exports = ngModule.name;

function ukaseData() {
    return {
        restrict: 'E',
        transclude: true,
        scope: {},
        bindToController: true,
        controllerAs: 'vm',
        controller: 'ukaseDataController',
        template: '<div data-flex data-layout="column">' +
            '  <div class="json-editor flex-grow" ></div>' +
            '  <div data-flex data-layout="row" class="flex-initial layout-align-center-center">' +
            '    <md-button class="md-raised md-primary" ng-click="send()">Update PDF</md-button>' +
            '    <md-button class="md-raised md-primary" ng-click="store()">Store json</md-button>' +
            '  </div>' +
            '</div>'
    };
}
