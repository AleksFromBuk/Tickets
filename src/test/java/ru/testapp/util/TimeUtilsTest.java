package ru.testapp.util;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ru.testapp.exception.CalculationException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Disabled
class TimeUtilsTest {
    @Test
    void calculateDuration_NormalFlight() {
        Duration d = TimeUtils.calculateDuration(
                LocalDate.of(2023, 1, 1), LocalTime.of(10, 0),
                LocalDate.of(2023, 1, 1), LocalTime.of(15, 30),
                "VVO", "TLV"
        );
        assertEquals(5, d.toHours());
        assertEquals(30, d.toMinutesPart());
    }

    @Test
    void calculateDuration_NextDayArrival() {
        Duration d = TimeUtils.calculateDuration(
                LocalDate.of(2023, 1, 1), LocalTime.of(23, 0),
                LocalDate.of(2023, 1, 2), LocalTime.of(5, 0),
                "VVO", "TLV"
        );
        assertEquals(6, d.toHours());
    }

    @Test
    void calculateDuration_InvalidDuration() {
        assertThrows(CalculationException.class, () ->
                TimeUtils.calculateDuration(
                        LocalDate.of(2023, 1, 2), LocalTime.of(10, 0),
                        LocalDate.of(2023, 1, 1), LocalTime.of(8, 0),
                        "VVO", "TLV"
                )
        );
    }

}