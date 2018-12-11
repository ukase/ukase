package com.github.ukase.toolkit.helpers.datetime;

import com.github.jknack.handlebars.Options;
import com.github.ukase.toolkit.helpers.AbstractHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.*;
import java.util.*;
import java.util.function.*;

/**
 * Add specified date period @{@link PeriodType} to the specified date
 */
@Slf4j
@Component
public class AddPeriodHelper extends AbstractHelper<Object> {
    private static final String CHANGE_DATETIME = "add_period";

    private static final Map<PeriodType, BiFunction<OffsetDateTime, Long, OffsetDateTime>> operations = new HashMap<>();

    static {
        operations.put(PeriodType.year, OffsetDateTime::plusYears);
        operations.put(PeriodType.month, OffsetDateTime::plusMonths);
        operations.put(PeriodType.day, OffsetDateTime::plusDays);
        operations.put(PeriodType.hour, OffsetDateTime::plusHours);
        operations.put(PeriodType.min, OffsetDateTime::plusMinutes);
        operations.put(PeriodType.sec, OffsetDateTime::plusSeconds);
    }

    public AddPeriodHelper() {
        super(CHANGE_DATETIME);
    }

    @Override
    public Object apply(Object context, Options options) throws IOException {
        try {
            if (StringUtils.isEmpty((String)context)) {
                return "";
            }

            if( options.params.length != 2 ) {
                log.error(MessageFormat.format(
                        "Incorrect count of input params ={0} (expected={1}, params={2}",
                        options.params.length, 2, Arrays.toString(options.params) ) );
                return "";
            }


            OffsetDateTime date = parseDateTime((String)context);
            PeriodType periodType = PeriodType.valueOf(options.param(0));
            Integer period = options.param(1);

            return operations.get(periodType).apply(date, period.longValue());
        } catch (Exception e) {
            log.error("Can not evaluate.", e);
            return "";
        }
    }

    private OffsetDateTime parseDateTime(String val) {
        return OffsetDateTime.parse(val);
    }

}
