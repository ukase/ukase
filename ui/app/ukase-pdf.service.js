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
        'ukasePoller',
        'ukaseFactory',
        ukasePdfService]);

};

function ukasePdfService($http, $q, $log, poller, factory) {
    function send(json) {
        var defer = $q.defer(),
            requestUrl = '/api/pdf/';

        $http({
            method: 'POST',
            data: json,
            responseType: 'arraybuffer',
            url: encodeURI(requestUrl)
        }).success(function (data) {
            var file = new Blob([data], {type: 'application/pdf'}),
                fileUrl = URL.createObjectURL(file);
            defer.resolve(fileUrl);
        }).error(function (error) {
            defer.reject(error);
        });

        return defer.promise;
    }

    return {
        pdfData: undefined,
        startPolling: function () {
            while (poller.flag) {
                if (poller.poll() === 'updated') {
                    this.pdfData = send(factory.json);
                }
            }
        },
        startAutoUpdate: function () {
            //var interval;
            while (factory.flag) {
                //interval = setInterval()
                //todo
            }
            //clearInterval(interval);
        },
        sendData: function () {
            this.pdfData = send(factory.json);
        }
    };
}

