package ru.skillbox.diplom.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeUtil {

    public static long getCurrentTimestampUtc() {
        return zonedDateTimeToLong(ZonedDateTime.now());
    }

    public static long zonedDateTimeToLong(ZonedDateTime zonedDateTime) {
        return ZonedDateTime.ofInstant(zonedDateTime.toInstant(), ZoneId.of("UTC")).toEpochSecond();
    }
}
