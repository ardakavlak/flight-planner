package com.arda.flightplanner.util;

import java.util.Calendar;
import java.util.Date;

public final class DateUtil {

    private DateUtil() {}

    public static Date getDatePlusDuration(Date date, int durationInMinutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, durationInMinutes);
        return calendar.getTime();
    }
}
