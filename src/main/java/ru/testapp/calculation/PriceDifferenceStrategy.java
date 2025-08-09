package ru.testapp.calculation;

import ru.testapp.domain.Ticket;
import ru.testapp.service.CalculationType;

import java.util.List;
import java.util.stream.Stream;

/**
 * Стратегия: разница между средним и медианой цен для маршрута.
 */
public class PriceDifferenceStrategy implements CalculationStrategy<Double> {
    private final String origin;
    private final String destination;

    public PriceDifferenceStrategy(String origin, String destination) {
        this.origin = origin;
        this.destination = destination;
    }

    @Override
    public Double calculate(Stream<Ticket> tickets) {
        if (tickets == null) {
            return 0d;
        }
        List<Integer> prices = tickets
                .filter(t -> origin.equalsIgnoreCase(t.getOrigin())
                        && destination.equalsIgnoreCase(t.getDestination()))
                .map(Ticket::getPrice)
                .sorted()
                .toList();

        if (prices.isEmpty() || prices.size() == 1) {
            return 0d;
        }
        double average = prices.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        double median;
        int n = prices.size();
        if (n % 2 == 0) {
            median = (prices.get(n / 2 - 1) + prices.get(n / 2)) / 2.0;
        } else {
            median = prices.get(n / 2);
        }
        return average - median;
    }

    @Override
    public String name() {
        return CalculationType.PRICE_DIFFERENCE.name();
    }
}
