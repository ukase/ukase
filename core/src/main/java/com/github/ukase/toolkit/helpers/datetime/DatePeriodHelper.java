package com.github.ukase.toolkit.helpers.datetime;

import com.github.jknack.handlebars.Options;
import com.github.ukase.toolkit.helpers.AbstractHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.time.*;

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
            if (context == null && options.params.length < 2)
                return "";

            OffsetDateTime dateFrom = ( !StringUtils.isEmpty((String)context ))
                    ? parseDateTime((String)context)
                    : OffsetDateTime.now();
            PeriodType periodType = PeriodType.valueOf(options.param(0));
            OffsetDateTime dateTo = extractDateTimeOrCurrent(options, 1);

            Period period = Period.between(dateFrom.toLocalDate(), dateTo.toLocalDate());

            switch (periodType) {
                case day:
                    return period.getDays();
                default:
                    throw new RuntimeException("Unsupported period: " + periodType);
            }
        } catch (Exception e) {
            log.error("Can not evaluate.", e);
            return null;
        }
    }

    private OffsetDateTime extractDateTimeOrCurrent(Options options, int index) {
        if( options.params.length <= index)
            return OffsetDateTime.now();
        String val = options.param(1 );
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

