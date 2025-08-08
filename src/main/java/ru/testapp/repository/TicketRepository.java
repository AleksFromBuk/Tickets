package ru.testapp.repository;

import ru.testapp.domain.Ticket;

import java.util.stream.Stream;

/**
 * Репозиторий, дающий Stream<Ticket>. Внедряется реализация JsonTicketRepository.
 * Поток необходимо закрывать (try-with-resources).
 */
public interface TicketRepository {
    Stream<Ticket> streamTickets();
}
