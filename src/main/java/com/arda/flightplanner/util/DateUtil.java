package com.arda.flightplanner.util;

import java.time.LocalDateTime;

public final class DateUtil {

    private DateUtil() {}

    public static LocalDateTime getDatePlusDuration(LocalDateTime date, int durationInMinutes) {
        return date.plusMinutes(durationInMinutes);
    }
}
