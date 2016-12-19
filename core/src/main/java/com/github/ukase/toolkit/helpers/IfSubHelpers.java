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

package com.github.ukase.toolkit.helpers;

import com.github.jknack.handlebars.Options;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

abstract class IfSubHelpers<T> extends AbstractHelper<T> {
    IfSubHelpers(String name) {
        super(name);
    }

    @Override
    public CharSequence apply(T context, Options options) throws IOException {
        return test(context, options) ? "true" : null;
    }

    public abstract boolean test(T context, Options options) throws IOException;

    @Component
    public static class EqHelper extends IfSubHelpers<Object> {
        public EqHelper() {
            super("eq");
        }

        @Override
        public boolean test(Object context, Options options) throws IOException {
            Object parameter = options.param(0, null);
            return parameter != null && parameter.equals(context);
        }
    }

    @Component
    public static class InHelper extends IfSubHelpers<Object> {
        public InHelper() {
            super("in");
        }

        @Override
        public boolean test(Object context, Options options) throws IOException {
            return Arrays.stream(options.params)
                    .anyMatch(parameter -> parameter != null && parameter.equals(context));
        }
    }

    @Component
    public static class NeHelper extends IfSubHelpers<Object> {
        public NeHelper() {
            super("ne");
        }

        @Override
        public boolean test(Object context, Options options) throws IOException {
            Object parameter = options.param(0, null);
            return parameter != null && !parameter.equals(context);
        }
    }

    static abstract class ComparableSubHelper<T> extends IfSubHelpers<T> {
        ComparableSubHelper(String name) {
            super(name);
        }

        public abstract boolean compare(Comparable object, Comparable against);

        @Override
        public boolean test(Object context, Options options) throws IOException {
            Object parameter = options.param(0, null);
            return parameter instanceof Comparable
                    && context instanceof Comparable
                    && compare((Comparable) context, (Comparable) parameter);
        }
    }

    @Component
    public static class LtHelper extends ComparableSubHelper<Object> {
        public LtHelper() {
            super("lt");
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean compare(Comparable object, Comparable against) {
            return object.compareTo(against) < 0;
        }
    }

    @Component
    public static class LteHelper extends ComparableSubHelper<Object> {
        public LteHelper() {
            super("lte");
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean compare(Comparable object, Comparable against) {
            return object.compareTo(against) <= 0;
        }
    }

    @Component
    public static class GtHelper extends ComparableSubHelper<Object> {
        public GtHelper() {
            super("gt");
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean compare(Comparable object, Comparable against) {
            return object.compareTo(against) > 0;
        }
    }

    @Component
    public static class GteHelper extends ComparableSubHelper<Object> {
        public GteHelper() {
            super("gte");
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean compare(Comparable object, Comparable against) {
            return object.compareTo(against) >= 0;
        }
    }

    @Component
    public static class AndHelper extends IfSubHelpers<Object> {
        public AndHelper() {
            super("and");
        }

        @Override
        public boolean test(Object context, Options options) throws IOException {
            boolean testParameters = Arrays.stream(options.params).allMatch(Objects::nonNull);
            return testParameters || context != null;
        }
    }

    @Component
    public static class OrHelper extends IfSubHelpers<Object> {
        public OrHelper() {
            super("or");
        }

        @Override
        public boolean test(Object context, Options options) throws IOException {
            boolean testParameters = Arrays.stream(options.params).anyMatch(Objects::nonNull);
            return testParameters || context != null;
        }
    }

    @Component
    public static class NotHelper extends IfSubHelpers<Object> {
        public NotHelper() {
            super("not");
        }

        @Override
        public boolean test(Object context, Options options) throws IOException {
            return context == null;
        }
    }
}
