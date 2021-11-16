package ru.skillbox.diplom.util;

import java.time.*;
import java.util.Date;

public class TimeUtil {
    private static final ZoneId ZONE_UTC = ZoneId.of("UTC");

    public static long getCurrentTimestampUtc() {
        return zonedDateTimeToLong(ZonedDateTime.now());
    }

    public static long zonedDateTimeToLong(ZonedDateTime zonedDateTime) {
        return ZonedDateTime.ofInstant(zonedDateTime.toInstant(), ZONE_UTC).toEpochSecond();
    }
    public static ZonedDateTime getZonedDateTimeFromMillis(Long millis) {
        return millis == null ? null :  ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZONE_UTC);
    }

    public static LocalDateTime DateToLocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZONE_UTC)
                .toLocalDateTime();
    }

    public static Date LocalDateToDate(LocalDate dt) {
        return java.util.Date.from(dt.atStartOfDay()
                .atZone(ZONE_UTC)
                .toInstant());
    }
}
