package ru.testapp.calculation;

import ru.testapp.domain.Ticket;
import ru.testapp.service.CalculationType;
import ru.testapp.util.TimeUtils;

import java.time.Duration;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Стратегия: минимальное время полёта по перевозчику для заданной пары городов.
 */
public class MinFlightTimeStrategy implements CalculationStrategy<Map<String, Duration>> {
    private final String origin;
    private final String destination;

    public MinFlightTimeStrategy(String origin, String destination) {
        this.origin = origin;
        this.destination = destination;
    }

    @Override
    public Map<String, Duration> calculate(Stream<Ticket> tickets) {
        if (tickets == null) {
            return Map.of();
        }
        Map<String, Duration> result = tickets
                .filter(t -> origin.equalsIgnoreCase(t.getOrigin()) && destination.equalsIgnoreCase(t.getDestination()))
                .collect(Collectors.groupingBy(
                        Ticket::getCarrier,
                        Collectors.mapping(
                                (Ticket t) -> TimeUtils.calculateDuration(
                                        t.getDepartureDate(),
                                        t.getDepartureTime(),
                                        t.getArrivalDate(),
                                        t.getArrivalTime(),
                                        origin, destination
                                ),
                                Collectors.minBy(Comparator.naturalOrder())
                        )
                ))
                .entrySet().stream()
                .filter(e -> e.getValue().isPresent())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().get(),
                        (a, b) -> a,
                        TreeMap::new
                ));
        return result;
    }

    @Override
    public String name() {
        return CalculationType.MIN_FLIGHT_TIME.name();
    }
}
