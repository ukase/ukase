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

package com.github.ukase.web.validation;

import com.github.ukase.toolkit.UkaseTemplateLoader;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;

@Component
@AllArgsConstructor
public class HtmlTemplateLocationExistsValidator implements ConstraintValidator<HtmlTemplateLocationExists, String> {
    private final Collection<UkaseTemplateLoader> templateLoaders;

    @Override
    public void initialize(HtmlTemplateLocationExists constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return templateLoaders.stream()
                .map(l -> l.hasTemplate(value))
                .reduce((has1, has2) -> has1 || has2).orElse(false);
    }
}
