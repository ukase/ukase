package com.github.ukase.toolkit.helpers.datetime;

import com.github.jknack.handlebars.Options;
import com.github.ukase.toolkit.helpers.AbstractHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Evaluate period between two dates.
 * Dates without value (null) will be replaced with current time
 */
@Slf4j
@Component
public class DatePeriodHelper extends AbstractHelper<Object> {
    private static final String DATETIME_PERIOD = "datetime_period";

    public DatePeriodHelper() {
        super(DATETIME_PERIOD);
    }

    @Override
    public Object apply(Object context, Options options) {
        try {
            if (context == null && options.params.length < 2) {
                return "";
            }

            OffsetDateTime dateFrom = ( !StringUtils.isEmpty((String)context ))
                    ? parseDateTime((String)context)
                    : OffsetDateTime.now();
            PeriodType periodType = PeriodType.valueOf(options.param(0));
            OffsetDateTime dateTo = extractDateTimeOrCurrent(options, 1);

            switch (periodType) {
                case year:
                    return ChronoUnit.YEARS.between(dateFrom, dateTo);
                case month:
                    return ChronoUnit.MONTHS.between(dateFrom, dateTo);
                case day:
                    return ChronoUnit.DAYS.between(dateFrom, dateTo);
                case hour:
                    return ChronoUnit.HOURS.between(dateFrom, dateTo);
                case min:
                    return ChronoUnit.MINUTES.between(dateFrom, dateTo);
                case sec:
                    return ChronoUnit.SECONDS.between(dateFrom, dateTo);
                default:
                    log.error("Date time period {} is not supported.", periodType);
                    return "";

            }
        } catch (Exception e) {
            log.error("Can not evaluate.", e);
            return "";
        }
    }

    private OffsetDateTime extractDateTimeOrCurrent(Options options, int index) {
        if( options.params.length <= index) {
            return OffsetDateTime.now();
        }
        String val = options.param(1);
        if(StringUtils.isEmpty(val)) {
            return OffsetDateTime.now();
        } else {
            return parseDateTime(val);
        }
    }

    private OffsetDateTime parseDateTime(String val) {
        return OffsetDateTime.parse(val);
    }


}

