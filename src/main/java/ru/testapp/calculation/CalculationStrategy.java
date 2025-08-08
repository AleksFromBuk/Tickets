package ru.testapp.calculation;

import ru.testapp.domain.Ticket;
import java.util.stream.Stream;

/**
 *
 * @param <T> Тип результата
 */
public interface CalculationStrategy<T> {
    /**
     * выполняет вычисление
     * @param tickets поток билетов (Свежий stream)
     * @return результат вычисления
     */
    T calculate(Stream<Ticket> tickets);

    /**
     * @return человеко-читаемое имя стратегии, используемое
     * в регистрациях/форматировании.
     */
    String name();

}
