package ru.testapp.service;

import ru.testapp.calculation.CalculationStrategy;
import ru.testapp.repository.TicketRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * усиленная типизация...
 */
public class AnalysisService {
    private final TicketRepository repository;
    private final Map<CalculationType, CalculationStrategy<?>> registry = new ConcurrentHashMap<>();
}
