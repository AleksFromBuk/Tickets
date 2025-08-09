package ru.testapp.service;

import ru.testapp.calculation.CalculationStrategy;
import ru.testapp.domain.Ticket;
import ru.testapp.exception.CalculationException;
import ru.testapp.repository.TicketRepository;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Сервис управления стратегиями и исполнением.
 * (усиленная типизация)
 */
public class AnalysisService {
    private final TicketRepository repository;
    private final Map<CalculationType, CalculationStrategy<?>>
            registry = new ConcurrentHashMap<>();

    public AnalysisService(TicketRepository repository) {
        this.repository = repository;
    }

    /**
     * Регистрирует стратегию для типа вычисления.
     */
    public <T> void register(CalculationType type, CalculationStrategy<T> strategy) {
        registry.put(type, strategy);
    }

    /**
     * Выполнить одну стратегию; поток закрывается автоматически.
     * @param type тип расчёта
     * @param origin origin code
     * @param destination destination code
     * @return результат
     * @param <T> тип результата
     */
    @SuppressWarnings("unchecked")
    public <T> T calculate(CalculationType type, String origin, String destination) {
        CalculationStrategy<T> strategy = (CalculationStrategy<T>) registry.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy not registered for " + type);
        }
        try (Stream<Ticket> stream = repository.streamTickets()) {
            return strategy.calculate(stream);
        } catch (RuntimeException e) {
            throw new CalculationException("Calculation failed for " + type, e, 5);
        }
    }

    /**
     * Выполнить все зарегистрированные стратегии и вернуть результат в порядке регистрации.
     */
    public Map<String,  Object> calculateAll(String origin, String destination) {
        Map<String, Object> results = new LinkedHashMap<>();
        for (Map.Entry<CalculationType, CalculationStrategy<?>> entry : registry.entrySet()) {
            Object result = calculate(entry.getKey(), origin, destination);
            results.put(entry.getKey().name(), result);
        }
        return results;
    }
}
