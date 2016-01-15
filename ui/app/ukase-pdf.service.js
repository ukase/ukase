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
    ngModule.service('ukasePdfService', [
        '$http',
        '$q',
        '$log',
        '$sce',
        'ukasePoller',
        'ukaseFactory',
        '$mdToast',
        ukasePdfService]);

};

function ukasePdfService($http, $q, $log, $sce, poller, factory, $mdToast) {
    var service = {
        pdfData: undefined,
        autoUpdate: false
    };

    function send(json) {
        var defer = $q.defer(),
            requestUrl = '/api/pdf/';
        $log.debug('pdf post data: ' + json);

        $http({
            method: 'POST',
            data: json,
            responseType: 'arraybuffer',
            accepts: 'application/pdf',
            url: encodeURI(requestUrl)
        }).success(function (data) {
            var file = new Blob([data], {type: 'application/pdf'}),
                fileUrl = URL.createObjectURL(file);
            $log.debug('success: ' + fileUrl);
            defer.resolve($sce.trustAsResourceUrl(fileUrl));
        }).error(function (error, code) {
            var message = code === 406 ? 'Error in JSON data' : 'Error with rendering on server';
            $mdToast.show({
                template: '<md-toast><span flex>Error on load: ' + message + '</span></md-toast>',
                position: 'top right'
            });
            $log.warn(message);
            defer.reject(error);
        });

        return defer.promise;
    }

    function checkPoller() {
        poller.poll('ANY').then(
            function(result) {
                if (result === 'updated') {
                    service.sendData();
                    checkPoller();
                } else {
                    checkPoller();
                }
            }
        );
    }

    service.startPolling = checkPoller;
    service.setAutoUpdate = function (update) {
        this.autoUpdate = update;
    };
    service.sendData = function () {
        send(factory.json).then(
            function(data) {
                service.pdfData = data;
            }
        );
    };
    service.getData = function() {
        return service.pdfData;
    };
    service.isAutoUpdate = function() {
        return this.autoUpdate;
    };

    return service;
}

