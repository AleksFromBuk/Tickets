package ru.testapp.util;

import ru.testapp.config.AirportConfig;
import ru.testapp.exception.CalculationException;

import java.time.*;

/**
 * Время считаем с учётом ZoneId аэропортов — это "физическая длительность" полёта.
 */
public final class TimeUtils {

    public static Duration calculateDuration(
            java.time.LocalDate depDate, java.time.LocalTime depTime,
            java.time.LocalDate arrDate, java.time.LocalTime arrTime,
            String origin, String destination
    ) {
        ZoneId originZone = AirportConfig.getZone(origin);
        ZoneId destZone = AirportConfig.getZone(destination);

        ZonedDateTime dep = ZonedDateTime.of(depDate, depTime, originZone);
        ZonedDateTime arr = ZonedDateTime.of(arrDate, arrTime, destZone);

        if (arr.toInstant().isBefore(dep.toInstant())) {
            if (arr.toInstant().isBefore(dep.toInstant())) {
                throw new CalculationException("Неверная продолжительность полета: прибытие перед вылетом", 7);
            }
        }
        return Duration.between(dep.toInstant(), arr.toInstant());
    }

    public static String formatDuration(Duration d) {
        long hours = d.toHours();
        long minutes = d.toMinutesPart();
        return String.format("%dh %02dm", hours, minutes);
    }
}
