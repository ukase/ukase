package com.github.ukase.toolkit.helpers.datetime;

import com.github.jknack.handlebars.Options;
import com.github.ukase.toolkit.helpers.AbstractHelper;
import org.springframework.stereotype.Component;

import java.time.*;

/**
 * Evaluate period between two dates.
 * If one of the date is null, it will be replaced to current datetime
 */
@Component
public class DatePeriodHelper extends AbstractHelper<Object> {
    private static final String DATETIME_PERIOD = "datetime_period";

    public DatePeriodHelper() {
        super(DATETIME_PERIOD);
    }

    @Override
    public Object apply(Object context, Options options) {
        if( context == null && extractDateTo(options) == null)
            return  "";

        OffsetDateTime dateFrom = ( context != null ) ? (OffsetDateTime) context : OffsetDateTime.now();
        PeriodType periodType = PeriodType.valueOf( options.param(0) );
        OffsetDateTime dateTo = extractDateTo(options);

        Period period = Period.between(dateFrom.toLocalDate(), dateTo.toLocalDate());

        switch (periodType) {
            case day: return period.getDays();
            default: throw new RuntimeException("Unsupported period: " + periodType);
        }
    }

    private OffsetDateTime extractDateTo(Options options) {
        return options.param(1, OffsetDateTime.now() );
    }


}

