package ru.testapp.repository;

import ru.testapp.Ticket;

import java.util.List;

public interface TicketRepository {
    List<Ticket> findAll();

    List<Ticket> findByRoute(String origin, String destination);

}
