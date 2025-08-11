package ru.testapp.calculation;

import lombok.extern.slf4j.Slf4j;
import ru.testapp.domain.Ticket;
import ru.testapp.service.CalculationType;
import ru.testapp.util.TimeUtils;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Стратегия: минимальное время полёта по перевозчику для заданной пары городов.
 */
@Slf4j
public class MinFlightTimeStrategy implements CalculationStrategy<Map<String, Duration>> {
    @Override
    public Map<String, Duration> calculate(Stream<Ticket> tickets) {
        if (tickets == null) {
            log.warn("Данные не предоставлены...");
            return Collections.emptyMap();
        }
        Map<String, Optional<Duration>> grouped = tickets
                .filter(Objects::nonNull)
                //.filter(t -> origin.equalsIgnoreCase(t.getOrigin()) && destination.equalsIgnoreCase(t.getDestination()))
                .collect(Collectors.groupingBy(
                        Ticket::getCarrier,
                        Collectors.mapping(
                                (Ticket t) -> TimeUtils.calculateDuration(
                                        t.getDepartureDate(), t.getDepartureTime(),
                                        t.getArrivalDate(), t.getArrivalTime(),
                                        t.getOrigin(), t.getDestination()
                                ),
                                Collectors.minBy(Comparator.naturalOrder())
                        )
                ));

        // преобразуем в упорядочивающий TreeMap
        return grouped.entrySet().stream()
                .filter(e -> e.getValue().isPresent())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().get(),
                        (a,b) -> a,
                        TreeMap::new
                ));
    }

    @Override
    public String name() {
        return CalculationType.MIN_FLIGHT_TIME.name();
    }
}
