package com.github.ukase.toolkit.helpers.datetime;

import com.github.jknack.handlebars.Options;
import com.github.ukase.toolkit.helpers.AbstractHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.*;
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

            LocalDateTime dateFrom = (( !StringUtils.isEmpty((String)context ))
                    ? parseDateTime((String)context)
                    : OffsetDateTime.now()).toLocalDateTime();
            PeriodType periodType = PeriodType.valueOf(options.param(0));
            LocalDateTime dateTo = extractDateTimeOrCurrent(options, 1).toLocalDateTime();



            switch (periodType) {
                case year:
                    return ChronoUnit.YEARS.between(
                            createBeginOfYear(dateFrom),
                            createBeginOfYear(dateTo));
                case month:
                    return ChronoUnit.MONTHS.between(
                            createBeginOfMonth(dateFrom),
                            createBeginOfMonth(dateTo));
                case day:
                    return ChronoUnit.DAYS.between(
                            createBeginOfDay(dateFrom),
                            createBeginOfDay(dateTo));
                case hour:
                    return ChronoUnit.HOURS.between(
                            createBeginOfHour(dateFrom),
                            createBeginOfHour(dateTo));
                case min:
                    return ChronoUnit.MINUTES.between(
                            createBeginOfMinute(dateFrom),
                            createBeginOfMinute(dateTo));
                case sec:
                    return ChronoUnit.SECONDS.between(
                            createBeginOfSecond(dateFrom),
                            createBeginOfSecond(dateTo));
                default:
                    log.error("Date time period {} is not supported.", periodType);
                    return "";

            }
        } catch (Exception e) {
            log.error("Can not evaluate.", e);
            return "";
        }
    }

    private LocalDate createBeginOfYear(LocalDateTime dateFrom) {
        return LocalDate.of(dateFrom.getYear(), 1, 1);
    }

    private LocalDate createBeginOfMonth(LocalDateTime date) {
        return LocalDate.of(date.getYear(), date.getMonth(), 1);
    }

    private LocalDate createBeginOfDay(LocalDateTime date) {
        return LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth());
    }

    private LocalDateTime createBeginOfHour(LocalDateTime date) {
        return LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), date.getHour(), 0, 0);
    }

    private LocalDateTime createBeginOfMinute(LocalDateTime date) {
        return LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), date.getHour(), date.getMinute());
    }

    private LocalDateTime createBeginOfSecond(LocalDateTime date) {
        return LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), date.getHour(), date.getMinute(), date.getSecond());
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

