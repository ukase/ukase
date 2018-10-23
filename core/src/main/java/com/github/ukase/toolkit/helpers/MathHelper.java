package com.github.ukase.toolkit.helpers;

import com.github.jknack.handlebars.Options;

import java.io.IOException;
import java.math.BigDecimal;

public class MathHelper extends AbstractHelper<Object>  {

    public MathHelper() {
        super("math");
    }

    @Override
    public Object apply(Object context, Options options) throws IOException {
        BigDecimal leftVal = new BigDecimal(String.valueOf(context));
        String operation = options.param(0);
        BigDecimal rightVal = new BigDecimal((String)options.param(1));
        Integer scale = options.param(2);
        BigDecimal result = null;
        switch (operation) {
            case "*": result = leftVal.multiply(rightVal); break;
            default: throw new RuntimeException("Unsupported operation: " + operation);
        }

        if( scale != null ) {
            return result.setScale(scale);
        } else {
            return result;
        }
    }
}


