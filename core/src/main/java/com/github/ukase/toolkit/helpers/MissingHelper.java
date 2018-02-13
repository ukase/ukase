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

package com.github.ukase.toolkit.helpers;

import com.github.jknack.handlebars.HelperRegistry;
import com.github.jknack.handlebars.Options;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class MissingHelper extends AbstractHelper<Object> {
    public MissingHelper() {
        super(HelperRegistry.HELPER_MISSING);
    }

    @Override
    public Object apply(Object context, Options options) throws IOException {
        String helperName = options.helperName;
        if (options.handlebars.helper(helperName) != null) {
            logHelperError(helperName);
        } else {
            logDateEmpty(helperName);
        }

        return null;
    }

    private void logHelperError(String helperName) {
        log.warn("Missed helper: '" + helperName + "'");
    }

    private void logDateEmpty(String dataName) {
        log.info("Missed data: '" + dataName + "'");
    }
}
