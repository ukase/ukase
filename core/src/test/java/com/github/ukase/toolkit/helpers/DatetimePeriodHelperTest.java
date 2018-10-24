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
    public void testYearsWithDateToNullInTemplate() throws IOException {
        int years = 33;
        test("{{datetime_period data.value 'year'}}",
                createDataInDataContianer( OffsetDateTime.now().minusYears(years) ),
                String.valueOf(years)
        );
    }

    @Test
    public void testMonthsWithDateToNullInTemplate() throws IOException {
        int months = 33;
        test("{{datetime_period data.value 'month'}}",
                createDataInDataContianer( OffsetDateTime.now().minusMonths(months) ),
                String.valueOf(months)
        );
    }

    @Test
    public void testDaysWithDateFromNullInTemplate() throws IOException {
        int days = 33;
        test("{{datetime_period (null v) 'day' data.value}}",
                createDataInDataContianer( OffsetDateTime.now().minusDays(days) ),
                String.valueOf(-days)
        );
    }

    @Test
    public void testDaysWithDateToNullInTemplate() throws IOException {
        int days = 33;
        test("{{datetime_period data.value 'day'}}",
                createDataInDataContianer( OffsetDateTime.now().minusDays(days) ),
                String.valueOf(days)
        );
    }

    @Test
    public void testHoursWithDateToNullInTemplate() throws IOException {
        int hours = 33;
        test("{{datetime_period data.value 'hour'}}",
                createDataInDataContianer( OffsetDateTime.now().minusHours(hours) ),
                String.valueOf(hours)
        );
    }

    @Test
    public void testMinutsWithDateToNullInTemplate() throws IOException {
        int minuts = 33;
        test("{{datetime_period data.value 'min'}}",
                createDataInDataContianer( OffsetDateTime.now().minusMinutes(minuts) ),
                String.valueOf(minuts)
        );
    }

    @Test
    public void testSecWithDateToNullInTemplate() throws IOException {
        int seconds = 33;
        test("{{datetime_period data.value 'sec'}}",
                createDataInDataContianer( OffsetDateTime.now().minusSeconds(seconds) ),
                String.valueOf(seconds)
        );
    }
}

