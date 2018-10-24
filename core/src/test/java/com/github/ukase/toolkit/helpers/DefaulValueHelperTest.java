package com.github.ukase.toolkit.helpers;


import com.github.jknack.handlebars.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static com.github.ukase.toolkit.helpers.OptionsFactory.getOptions;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest()
public class DefaulValueHelperTest extends BaseHelperTest {
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


}
