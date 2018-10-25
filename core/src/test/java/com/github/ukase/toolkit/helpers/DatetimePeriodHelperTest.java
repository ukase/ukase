package com.github.ukase.toolkit.helpers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest()
public class DatetimePeriodHelperTest extends BaseHelperTest {

    @Test
    public void testYearsWithDateToNullInTemplate() throws IOException {
        int years = 33;
        test("{{datetime_period data.value 'year'}}",
                createDataInDataContianer( OffsetDateTime.now().minusYears(years).plusSeconds(1) ),
                String.valueOf(years)
        );
    }

    @Test
    public void testMonthsWithDateToNullInTemplate() throws IOException {
        int months = 33;
        test("{{datetime_period data.value 'month'}}",
                createDataInDataContianer( OffsetDateTime.now().minusMonths(months).plusSeconds(1) ),
                String.valueOf(months)
        );
    }

    @Test
    public void testDaysWithDateFromNullInTemplate() throws IOException {
        int days = 33;
        test("{{datetime_period (null v) 'day' data.value}}",
                createDataInDataContianer( OffsetDateTime.now().minusDays(days).plusSeconds(1) ),
                String.valueOf(-days)
        );
    }

    @Test
    public void testDaysWithDateToNullInTemplate() throws IOException {
        int days = 33;
        test("{{datetime_period data.value 'day'}}",
                createDataInDataContianer( OffsetDateTime.now().minusDays(days).plusSeconds(1) ),
                String.valueOf(days)
        );
    }

    @Test
    public void testHoursWithDateToNullInTemplate() throws IOException {
        int hours = 33;
        test("{{datetime_period data.value 'hour'}}",
                createDataInDataContianer( OffsetDateTime.now().minusHours(hours).plusSeconds(1) ),
                String.valueOf(hours)
        );
    }

    @Test
    public void testMinutesWithDateToNullInTemplate() throws IOException {
        int minutes = 33;
        OffsetDateTime now = OffsetDateTime.now();
        Map data = new HashMap();
        data.putAll(createDataInDataContianer( "from", now.minusMinutes(minutes).plusSeconds(1) ));
        data.putAll(createDataInDataContianer( "to", now ));
        test("{{datetime_period from.value 'min' to.value}}",
                data,
                String.valueOf(minutes)
        );
    }

    @Test
    public void testSecWithDateToNullInTemplate() throws IOException {
        int seconds = 33;
        Map data = new HashMap();
        OffsetDateTime now = OffsetDateTime.now();
        data.putAll(createDataInDataContianer( "from", now.minusSeconds(seconds).plusNanos(1) ));
        data.putAll(createDataInDataContianer( "to", now ));
        test("{{datetime_period from.value 'sec' to.value}}",
                data,
                String.valueOf(seconds)
        );
    }


}

