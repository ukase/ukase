package com.github.ukase.toolkit.helpers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.OffsetDateTime;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest()
public class AddPeriodHelperTest extends BaseHelperTest {

    @Test
    public void testPlusYears() throws IOException {
        int years = 33;
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime expectedResult = now.plusYears(years);
        test("{{add_period data.value 'year' 33}}",
                createDataInDataContianer(now),
                expectedResult.toString()
        );
    }


    @Test
    public void testPlusMonths() throws IOException {
        int monthes = 33;
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime expectedResult = now.plusMonths(monthes);
        test("{{add_period data.value 'month' 33}}",
                createDataInDataContianer( OffsetDateTime.now() ),
                expectedResult.toString()
        );
    }

    @Test
    public void testPlusDays() throws IOException {
        int days = 33;
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime expectedResult = now.plusDays(days);
        test("{{add_period data.value 'day' 33}}",
                createDataInDataContianer( OffsetDateTime.now() ),
                expectedResult.toString()
        );
    }

    @Test
    public void testMinusDays() throws IOException {
        int days = 33;
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime expectedResult = now.minusDays(days);
        test("{{add_period data.value 'day' -33}}",
                createDataInDataContianer( OffsetDateTime.now() ),
                expectedResult.toString()
        );
    }

    @Test
    public void testPlusHours() throws IOException {
        int hours = 33;
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime expectedResult = now.plusHours(hours);
        test("{{add_period data.value 'hour' 33}}",
                createDataInDataContianer( OffsetDateTime.now() ),
                expectedResult.toString()
        );
    }

    @Test
    public void testPlusMinutes() throws IOException {
        int minutes = 150;
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime expectedResult = now.plusMinutes(minutes);
        test("{{add_period data.value 'min' 150}}",
                createDataInDataContianer( OffsetDateTime.now() ),
                expectedResult.toString()
        );
    }

    @Test
    public void testPlusSeconds() throws IOException {
        int seconds = 150;
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime expectedResult = now.plusSeconds(seconds);
        test("{{add_period data.value 'sec' 150}}",
                createDataInDataContianer( OffsetDateTime.now() ),
                expectedResult.toString()
        );
    }
}
