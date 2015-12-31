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

module.exports = function (ngModule) {
    ngModule.controller('ukaseMenuController', [
        '$scope',
        'ukasePoller',
        'ukasePdfService',
        'ukaseFactory',
        ukaseMenuController
    ]);
};

function ukaseMenuController($scope, poller, service, factory) {
    $scope.pollerEnabled = poller.flag;
    $scope.pollerClicked = function() {
        if ($scope.pollerEnabled) {
            service.startPolling();
        }
    };

    $scope.autoResourcesUpdate = factory.flag;
    $scope.autoResourcesClicked = function() {
        if ($scope.autoResourcesUpdate) {
            service.startAutoUpdate();
        }
    };
}
