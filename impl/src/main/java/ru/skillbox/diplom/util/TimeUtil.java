package ru.skillbox.diplom.util;

import java.time.Instant;
import java.time.ZoneId;

public class TimeUtil {

    public static long getCurrentTimestampUtc() {
        return Instant.now().atZone(ZoneId.of("UTC")).toEpochSecond();
    }
}
