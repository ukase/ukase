/*
 * Copyright (c) 2016 Konstantin Lepa <konstantin+ukase@lepabox.net>
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

package com.github.ukase.toolkit.xlsx;

import org.hamcrest.core.IsNot;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.parser.PropertyValue;
import org.xhtmlrenderer.css.style.FSDerivedValue;
import org.xhtmlrenderer.css.style.derived.StringValue;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

public abstract class MockedTests {
    protected FSDerivedValue stringValue(String value, CSSName cssName) {
        PropertyValue propertyValue = new PropertyValue(PropertyValue.VALUE_TYPE_STRING, 0, value);
        return new StringValue(cssName, propertyValue);
    }

    protected <T, R> void onlyFunction(T value, Function<T, R> func, Object mock, R result) {
        String wrongArgument = "Wrong argument - wait for " + value + " but got another";
        doReturn(result).when(mock);
        func.apply(value);
        doThrow(exception(wrongArgument)).when(mock);
        func.apply(not(value));
    }

    <T> void trySetNull(Consumer<T> consumer) {
        try {
            consumer.accept(null);
            throw new IllegalStateException("Null were successfully consumed - but shouldn't!");
        } catch (NullPointerException e) {
            //all ok;
        }
    }

    <T> void onlyValue(T value, Consumer<T> consumer, Object mock) {
        String wrongArgument = "Wrong argument - wait for " + value + " but got another";
        doNothing().when(mock);
        consumer.accept(value);
        doThrow(exception(wrongArgument)).when(mock);
        consumer.accept(not(value));
    }

    protected <T> T not(T object) {
        return argThat(IsNot.not(object));
    }

    private IllegalArgumentException exception(String message) {
        return new IllegalArgumentException(message);
    }
}
