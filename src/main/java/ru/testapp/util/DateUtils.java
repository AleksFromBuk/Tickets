package ru.testapp.util;

import ru.testapp.exception.ValidationException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Парсинг дат/времени в формате dd.MM.yy и H:mm
 */
public final class DateUtils {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yy");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("H:mm");

    public static LocalDate parseDateStrict(String s) {
        if (s == null) throw new ValidationException("Missing date field", 4);
        try {
            return LocalDate.parse(s, DATE);
        } catch (DateTimeParseException ex) {
            throw new ValidationException("Incorrect date format: " + s, 4);
        }
    }

    public static LocalTime parseTimeStrict(String s) {
        if (s == null) throw new ValidationException("Missing time field", 4);
        try {
            return LocalTime.parse(s, TIME);
        } catch (DateTimeParseException ex) {
            throw new ValidationException("Incorrect time format: " + s, 4);
        }
    }
}
