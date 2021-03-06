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

package com.github.ukase.toolkit.render;

import com.github.ukase.web.UkasePayload;
import lombok.Getter;
import lombok.Setter;

public class RenderException extends RuntimeException {
    @Getter
    @Setter
    private UkasePayload payload;
    @Getter
    private final String renderStep;

    public RenderException(String message, Throwable cause, String renderStep) {
        super(message, cause);
        this.renderStep = renderStep;
    }

    public RenderException(String message, String renderStep) {
        super(message);
        this.renderStep = renderStep;
    }
}
