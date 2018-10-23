package com.github.ukase.toolkit.helpers;

import com.github.jknack.handlebars.Options;
import com.github.ukase.toolkit.helpers.datetime.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest()
public class DatetimePeriodHelperTest extends BaseHelperTest {

    @Test
    public void testDaysWithDateFromNullInTemplate() throws IOException {
        int days = 3;
        test("{{datetime_period (null v) 'day' (proxy data.value)}}",
                createDataInDataContianer( OffsetDateTime.now().minusDays(days) ),
                String.valueOf(-days)
        );
    }

    @Test
    public void testDaysWithDateToNullInTemplate() throws IOException {
        int days = 3;
        test("{{datetime_period data.value 'day'}}",
                createDataInDataContianer( OffsetDateTime.now().minusDays(days) ),
                String.valueOf(days)
        );
    }
}

