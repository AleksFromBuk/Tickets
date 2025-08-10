package ru.testapp.output;

import ru.testapp.config.AirportConfig;
import ru.testapp.util.TimeUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;

/**
 * Форматирование результатов в человеко-читаемый вид (русский).
 */
public class ResultFormatter {

    /**
     * Формирует блок минимальных времен.
     */
    public String formatMinFlightTimes(Map<String, Duration> map, String origin, String destination) {
        ZoneId originZone = AirportConfig.getZone(origin);
        ZoneId destZone = AirportConfig.getZone(destination);

        String originOffset = formatOffset(originZone.getRules().getOffset(Instant.now()));
        String destOffset = formatOffset(destZone.getRules().getOffset(Instant.now()));

        StringBuilder sb = new StringBuilder();
        sb.append("Расчет выполнен на основе:\n");
        sb.append(String.format("- Временная зона вылета: %s (%s)\n",
                originZone.getId(), originOffset));
        sb.append(String.format("- Временная зона прилета: %s (%s)\n\n",
                destZone.getId(), destOffset));
        sb.append(String.format("Минимальное время полета (%s -> %s):\n", origin, destination));

        if (map == null || map.isEmpty()) {
            sb.append(" Нет данных\n");
            return sb.toString();
        } else {
            map.forEach((carrier, duration) -> {
                String zoneInfo = String.format(" (%s - %s)",
                        AirportConfig.getZone(origin).getId(),
                        AirportConfig.getZone(destination).getId());

                sb.append(String.format("  %s: %s%s%n",
                        carrier,
                        TimeUtils.formatDuration(duration),
                        zoneInfo));
            });
        }
        return sb.toString();
    }

    /**
     * Форматирует разницу среднее - медиана.
     */
    public String formatPriceDifference(double diff) {
        return String.format("Разница между средней ценой и медианой: %.2f%n", diff);
    }

    private String formatOffset(ZoneOffset offset) {
        String offsetString = offset.getId().replace("Z", "+00:00");
        if (offsetString.length() == 2) {
            offsetString = offsetString + ":00";
        }
        return "UTC" + offsetString;
    }
}
