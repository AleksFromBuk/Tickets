package ru.testapp.util;

import org.junit.jupiter.api.Test;
import ru.testapp.exception.CalculationException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;


class TimeUtilsTest {
    @Test
    void realDuration_specificSample() {
        // 12.05.2018, VVO 17:20 -> TLV 23:50  - duration = 13h30m (VVO UTC+10, TLV UTC+3 на текущую дату)
        LocalDate date = LocalDate.of(2018, 5, 12);
        Duration d = TimeUtils.calculateDuration(date, LocalTime.of(17, 20),
                date, LocalTime.of(23, 50), "VVO", "TLV");
        assertEquals(Duration.ofHours(13).plusMinutes(30), d);
    }

    @Test
    void realDuration_betweenVVOandTLV() {

        LocalDate d = LocalDate.of(2018, 5, 12);
        LocalTime dep = LocalTime.of(17, 20);
        LocalTime arr = LocalTime.of(23, 50);

        Duration dur = TimeUtils.calculateDuration(d, dep, d, arr, "VVO", "TLV");

        assertTrue(dur.toHours() >= 6 && dur.toHours() <= 20);
    }

    @Test
    void normalFlight_example() {
        LocalDate date = LocalDate.of(2018, 5, 12);
        Duration dur = TimeUtils.calculateDuration(
                date, LocalTime.of(10, 0),
                date, LocalTime.of(12, 30),
                "VVO", "TLV"
        );
        // Разница в UTC: VVO (UTC+10) 10:00 -> 00:00 UTC, TLV (UTC+3) 12:30 -> 09:30 UTC
        // Итог = 9h30m
        assertEquals(Duration.ofHours(9).plusMinutes(30), dur);
    }

    @Test
    void nextDayArrival_example() {
        LocalDate depDate = LocalDate.of(2018, 5, 12);
        LocalDate arrDate = LocalDate.of(2018, 5, 13);
        Duration dur = TimeUtils.calculateDuration(
                depDate, LocalTime.of(22, 0),
                arrDate, LocalTime.of(7, 0),
                "JFK", "LHR"
        );
        assertEquals(Duration.ofHours(5), dur); // фактическое по базе TZ
    }

    @Test
    void invalidDuration_throws() {
        LocalDate date = LocalDate.of(2018, 5, 12);
        assertThrows(CalculationException.class, () ->
                TimeUtils.calculateDuration(
                        date, LocalTime.of(12, 0),
                        date, LocalTime.of(11, 0),
                        "VVO", "VVO"
                )
        );
    }
}