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

var JsonEditor = require('jsoneditor');

require('jsoneditor_css');

module.exports = function (ngModule) {
    ngModule.controller('ukaseDataController', [
        '$scope',
        '$element',
        '$timeout',
        'ukaseFactory',
        'ukasePdfService',
        ukaseDataController
    ]);
};

function ukaseDataController($scope, $element, $timeout, factory, service) {
    var timeout = 1000/*ms*/,
        promise,
        options = {
            mode: 'tree',
            modes: ['code', 'tree', 'text'],
            title: 'JSON-data',
            onChange: jsonChanged
        },
        editor = new JsonEditor($element.find('.json-editor')[0], options);
    editor.setText(factory.json);

    $scope.send = function () {
        service.sendData();
    };
    $scope.store = function () {
        factory.saveToStorage();
    };

    function jsonChanged() {
        factory.json = editor.getText();
        if (service.isAutoUpdate()) {
            if (promise) {
                $timeout.cancel(promise);
            }
            $timeout(
                function () {
                    service.sendData();
                }, timeout
            );
        }
    }
}
