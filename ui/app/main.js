/*
 * Copyright (c) 2018 Pavel Uvarov <pauknone@yahoo.com>
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

require('./index.html');
require('angular-material/angular-material.css');
require('./assets/fonts/MaterialIcons-Regular.woff2');
require('./main.less');

ngModule = angular.module('ukase', [
    require('./config/main.config'),
    require('./config/theme.config'),
    require('./routes/home/home.route'),
    require('angular-material')
]);

require('./ukase.poller')(ngModule);
require('./ukase.factory')(ngModule);
require('./ukase-pdf.service')(ngModule);
