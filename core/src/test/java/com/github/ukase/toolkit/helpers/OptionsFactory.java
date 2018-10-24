package com.github.ukase.toolkit.helpers;

import com.github.jknack.handlebars.*;

import java.util.*;

public class OptionsFactory {

    public static Options getOptions(Object context, Object... params) {
        return new Options(null,
                "default_value",
                TagType.VAR,
                Context.newContext(context),
                null,
                null,
                params,
                new HashMap<>(),
                Collections.emptyList());
    }
}
