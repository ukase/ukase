package com.github.ukase.toolkit.helpers;

import com.github.jknack.handlebars.Options;
import com.github.ukase.toolkit.helpers.datetime.*;
import org.junit.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;

public class MathOperationTestHelper {

    @Test
    public void testMultiple() throws IOException {
        testOperation("*", BigDecimal.valueOf(5), BigDecimal.valueOf(10), 0, BigDecimal.valueOf(50));
    }

    private void testOperation(String operation, BigDecimal leftVal, BigDecimal rightVal, Integer scale, BigDecimal exprectedResult) throws IOException {
        MathHelper helper = new MathHelper();
        Object[] params = new Object[]{ operation, rightVal.toString(), scale };
        Options options = new Options(null, null, null, null, null, null, params, null, Collections.emptyList());
        Assert.assertEquals(exprectedResult, helper.apply(leftVal.toString(), options) );
    }
}
