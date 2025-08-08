package ru.testapp.util;

import ru.testapp.exception.ValidationException;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Парсинг дат/времён в требуемых форматах.
 */
public class DateUtils {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yy");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("H:mm");

    /**
     * Парсит дату в формате dd.MM.yy, иначе кидает ValidationException.
     */
    public static LocalDate parseDateStrict(String s) {
        try {
            return LocalDate.parse(s, DATE);
        } catch (DateTimeParseException ex) {
            throw new ValidationException("Incorrect date format: " + s, 4);
        }
    }

    /**
     * Парсит время в формате H:mm, иначе кидает ValidationException.
     */
    public static LocalTime parseTimeStrict(String s) {
        try {
            return LocalTime.parse(s, TIME);
        } catch (DateTimeException ex) {
            throw new ValidationException("Incorrect time format: " + TIME, 4);
        }
    }
}
