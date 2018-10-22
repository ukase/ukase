package com.github.ukase.toolkit.helpers.datetime;

import com.github.jknack.handlebars.Options;
import com.github.ukase.toolkit.helpers.AbstractHelper;
import org.springframework.stereotype.Component;

import java.time.*;

/**
 * Evaluate period between two dates.
 * By default end date is now.
 */
@Component
public class DatePeriodHelper extends AbstractHelper<Object> {
    private static final String DATETIME_PERIOD = "datetime_period";

    public DatePeriodHelper() {
        super(DATETIME_PERIOD);
    }

    @Override
    public Object apply(Object context, Options options) {
        if( context == null)
            return  "";
        OffsetDateTime dateFrom = (OffsetDateTime) context;
        PeriodType periodType = PeriodType.valueOf( options.param(0) );
        OffsetDateTime dateTo = options.param(1, OffsetDateTime.now() );

        Period period = Period.between(dateFrom.toLocalDate(), dateTo.toLocalDate());

        switch (periodType) {
            case day: return period.getDays();
            default: throw new RuntimeException("Unsupported period: " + periodType);
        }

    }
}

