package com.github.ukase.toolkit.helpers;

import com.github.jknack.handlebars.Options;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;

@Component
public class DefaultValueHelper extends AbstractHelper<Object> {
    private static final String HELPER_NAME = "default_value";
    public DefaultValueHelper() {
        super(HELPER_NAME);
    }

    @Override
    public Object apply(Object o, Options options) throws IOException {
        Assert.isTrue( options.params.length == 1,
                "It has to be 1 parameter - defaultValue. But now it is  " + options.params.length );
        Assert.notNull(options.param(0), "Default value is not allowed to be null");

        if( o == null)
            return options.param(0);
        return o;
    }
}
