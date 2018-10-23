package com.github.ukase.toolkit.helpers;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import org.junit.Test;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest()
public class ProxyHelperTest extends BaseHelperTest{

    @Test
    public void test() throws IOException {
        final String PROXY_VALUE = "proxy_value";
        test("{{proxy data.value}}", createDataInDataContianer(PROXY_VALUE), PROXY_VALUE);
    }
}
