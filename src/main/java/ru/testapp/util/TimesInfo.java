package ru.testapp.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimesInfo {

    public static long getDataTime(String departureDateStr, String departureTimeStr,
                                   String arrivalDateStr, String arrivalTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy H:mm");
        LocalDateTime departureDateTime = LocalDateTime.parse(departureDateStr + " " + departureTimeStr, formatter);
        LocalDateTime arrivalDateTime = LocalDateTime.parse(arrivalDateStr + " " + arrivalTimeStr, formatter);
        return Duration.between(departureDateTime, arrivalDateTime).toMinutes();
    }

    public static String getAnswerTemplate(long totalMinutes) {
        Duration duration = Duration.ofMinutes(totalMinutes);
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        return String.format("дни = %d, часы = %d, минуты = %d", days, hours, minutes);
    }
}
