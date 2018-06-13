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

package com.github.ukase.toolkit.pdf;

import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.pdf.ITextFontResolver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

class FontFamily {
    private String name;
    private List<ITextFontResolver.FontDescription> fontDescriptions = new ArrayList<>();

    void addFontDescription(ITextFontResolver.FontDescription descr) {
        fontDescriptions.add(descr);
        fontDescriptions.sort(Comparator.comparingInt(ITextFontResolver.FontDescription::getWeight));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    ITextFontResolver.FontDescription match(int desiredWeight, IdentValue style) {
        if (fontDescriptions.isEmpty()) {
            return null;
        }

        List<ITextFontResolver.FontDescription> candidates = fontDescriptions.stream()
                .filter(fontDescription -> fontDescription.getStyle() == style)
                .collect(Collectors.toList());

        if (candidates.size() == 0) {
            if (style == IdentValue.ITALIC) {
                return match(desiredWeight, IdentValue.OBLIQUE);
            } else if (style == IdentValue.OBLIQUE) {
                return match(desiredWeight, IdentValue.NORMAL);
            } else {
                candidates.addAll(fontDescriptions);
            }
        }

        ITextFontResolver.FontDescription result = SearchMode.SM_EXACT.find(candidates, desiredWeight);
        if (result == null) {
            if (desiredWeight <= 500) {
                result = SearchMode.SM_LIGHTER_OR_DARKER.find(candidates, desiredWeight);
            } else {
                result = SearchMode.SM_DARKER_OR_LIGHTER.find(candidates, desiredWeight);
            }
        }
        return result;
    }

    boolean flush() {
        fontDescriptions.removeIf(ITextFontResolver.FontDescription::isFromFontFace);
        return fontDescriptions.isEmpty();
    }

    private enum SearchMode {
        SM_EXACT(SearchMode::searchExact),
        SM_LIGHTER_OR_DARKER(SearchMode::searchLighterOrDarker),
        SM_DARKER_OR_LIGHTER(SearchMode::searchDarkerOrLighter);

        private final BiFunction<List<ITextFontResolver.FontDescription>, Integer, ITextFontResolver.FontDescription> function;

        SearchMode(BiFunction<List<ITextFontResolver.FontDescription>, Integer, ITextFontResolver.FontDescription> function) {
            this.function = function;
        }

        ITextFontResolver.FontDescription find(List<ITextFontResolver.FontDescription> matches, int desiredWeight) {
            return function.apply(matches, desiredWeight);
        }

        private static ITextFontResolver.FontDescription searchLighterOrDarker(List<ITextFontResolver.FontDescription> matches, int desiredWeight) {
            ITextFontResolver.FontDescription previous = null;
            for (ITextFontResolver.FontDescription description : matches) {
                if (description.getWeight() > desiredWeight) {
                    return previous != null ? previous : description;
                }
                previous = description;
            }
            return previous;
        }

        private static ITextFontResolver.FontDescription searchDarkerOrLighter(List<ITextFontResolver.FontDescription> matches, int desiredWeight) {
            ITextFontResolver.FontDescription prev = null;
            for (int offset = matches.size() - 1; offset >= 0; offset--) {
                ITextFontResolver.FontDescription description = matches.get(offset);
                if (description.getWeight() < desiredWeight) {
                    return prev != null ? prev : description;
                }
                prev = description;
            }
            return prev;
        }

        private static ITextFontResolver.FontDescription searchExact(List<ITextFontResolver.FontDescription> matches, int desiredWeight) {
            return matches.stream()
                    .filter(fontDescription -> fontDescription.getWeight() == desiredWeight)
                    .findAny()
                    .orElse(null);
        }
    }

}