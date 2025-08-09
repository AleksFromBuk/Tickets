package ru.testapp.output;

import ru.testapp.util.TimeUtils;

import java.time.Duration;
import java.util.Map;

/**
 * Форматирование результатов в человеко-читаемый вид (русский).
 */
public class ResultFormatter {

    /**
     * Формирует блок минимальных времен.
     */
    public String formatMinFlightTimes(Map<String, Duration> map, String origin, String destination) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Минимальное время полета (%s -> %s):%n", origin, destination));
        if (map == null || map.isEmpty()) {
            sb.append(" Нет данных\n");
            return sb.toString();
        }
        map.forEach((carrier, duration) ->
                sb.append(String.format(" %s: %s%n", carrier, TimeUtils.formatWork(duration)))
        );
        return sb.toString();
    }

    /**
     * Форматирует разницу среднее - медиана.
     */
    public String formatPriceDifference(double diff) {
        return String.format("Разница между средней ценой и медианой: %.2f%n", diff);
    }
}
