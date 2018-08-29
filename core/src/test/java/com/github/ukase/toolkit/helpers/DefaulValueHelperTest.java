package com.github.ukase.toolkit.helpers;


import com.github.jknack.handlebars.*;
import org.junit.*;

import java.io.IOException;
import java.util.*;

public class DefaulValueHelperTest {
    private static final Helper HELPER = new DefaultValueHelper();
    private static final String VALUE = "VALUE";
    private static final String DEFAULT_VALUE = "DEFAULT_VALUE";


    @Test
    public void defaultValueTest() throws IOException {
        String result = (String) HELPER.apply(null, getOptions( null, DEFAULT_VALUE));
        Assert.assertEquals( DEFAULT_VALUE, result);
    }

    @Test
    public void originalValueTest() throws IOException {
        String result = (String) HELPER.apply(VALUE, getOptions( VALUE, DEFAULT_VALUE));
        Assert.assertEquals( VALUE, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidParamsCountValueTest() throws IOException {
        String result = (String) HELPER.apply(null, getOptions(null));
        Assert.assertEquals( VALUE, result);
    }

    private Options getOptions(Object context, String... params) {
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
