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
    ngModule.factory('ukasePoller', ['$http', '$q', ukasePoller]);
};

function ukasePoller($http, $q) {
    var poller = {
        flag: false
    };

    function head(url) {
        return $http({
            method: 'HEAD',
            url: url
        });
    }

    poller.poll = function (template, defer) {
        var $d = !defer ? $q.defer() : defer,
            requestUrl = '/api/pdf/' + template,
            $request = head(encodeURI(requestUrl));

        $request.success(function () {
            $d.resolve('updated');
        });

        $request.error(function (error) {
            if (poller.flag) {
                poller.poll(template, $d);
            } else {
                $d.reject(error);
            }
        });

        return $d.promise;
    };

    return poller;
}
