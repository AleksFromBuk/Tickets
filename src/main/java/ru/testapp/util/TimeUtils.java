package ru.testapp.util;

import ru.testapp.config.AirportConfig;
import ru.testapp.exception.CalculationException;

import java.time.*;

public final class TimeUtils {
    public static Duration calculateDuration(LocalDate depDate, LocalTime depTime, LocalDate arrDate, LocalTime arrTime,
                                             String origin, String destination) {
        ZoneId originZone = AirportConfig.getZone(origin);
        ZoneId destZone = AirportConfig.getZone(destination);

        ZonedDateTime dep = ZonedDateTime.of(depDate, depTime, originZone);
        ZonedDateTime arr = ZonedDateTime.of(arrDate, arrTime, destZone);

        if (arr.isBefore(dep)) {
            int attempts = 0;
            while (arr.isBefore(dep) && attempts < AirportConfig.MAX_ATTEMPTS) {
                arr = arr.plusDays(1);
                attempts++;
            }

            if (arr.isBefore(dep)) {
                throw new CalculationException(
                        "Invalid flight duration: arrival before departure after " +
                                AirportConfig.MAX_ATTEMPTS + " correction attempts", 7
                );
            }
        }
        return Duration.between(dep, arr);
    }

    public static String formatWork(Duration d) {
        long hours = d.toHours();
        long minutes = d.toMinutesPart();
        return String.format("%dh %02dm", hours, minutes);
    }
}
