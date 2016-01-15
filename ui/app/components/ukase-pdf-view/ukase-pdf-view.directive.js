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

ngModule = angular.module('ukase.components.ukase-pdf-view',
    [

    ])
    .directive('ukasePdfView', ukasePdfView);

require('./ukase-pdf-view.controller')(ngModule);

module.exports = ngModule.name;

require('./ukase-pdf-view.less');

function ukasePdfView() {
    return {
        restrict: 'E',
        transclude: true,
        scope: {},
        bindToController: true,
        controllerAs: 'vm',
        controller: 'ukasePdfViewController',
        template: '<md-whiteframe class="md-whiteframe-2dp flex-grow layout-align-center-center preview" data-flex data-layout="column">' +
                '   <div class="flex-grow">' +
                '     <object data-ng-if="pdfData" data="{{pdfData}}" type="application/pdf"></object> ' +
                '   </div>' +
                '   <span data-ng-if="!pdfData">pdf preview (not loaded yet)</span>' +
                '</md-whiteframe>'
    };
}
