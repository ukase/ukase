package helpers;

import com.github.jknack.handlebars.Options;
import com.github.ukase.toolkit.helpers.datetime.*;
import org.junit.Assert;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Collections;

import org.junit.Test;

public class DatetimePeriodHelperTest {

    @Test
    public void testDays() throws IOException {
        int period = 5;
        OffsetDateTime tillNow = OffsetDateTime.now().minusDays(period);
        testPeriod(PeriodType.day.name(), tillNow, null, period);
    }

    private void testPeriod(String periodType, OffsetDateTime dateFrom, OffsetDateTime dateTo, int expected) throws IOException {
        DatePeriodHelper helper = new DatePeriodHelper();
        Object[] params = (dateTo== null) ?
                new Object[] { periodType }
                : new Object[] { periodType, dateTo };
        Options options = new Options(null, null, null, null, null, null, params, null, Collections.emptyList());
        Assert.assertEquals(expected, helper.apply(dateFrom, options) );
    }
}
