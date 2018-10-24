package com.github.ukase.toolkit.helpers;

import com.github.jknack.handlebars.Options;
import org.springframework.stereotype.Component;

/**
 * Helper is used as inner helper to extract variable from the context.
 */
@Component
public class ProxyHelper extends AbstractHelper<Object>  {
    public ProxyHelper() {
        super("proxy");
    }

    @Override
    public Object apply(Object context, Options options) {
        return context;
    }
}
