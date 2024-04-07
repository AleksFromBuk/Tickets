package ru.testapp;

import ru.testapp.repository.TicketRepository;

import java.util.List;

public class TicketService {
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public int findMinPriceForTargetDirection(String origin, String destination) {
        List<Ticket> tickets = ticketRepository.findByRoute(origin, destination);
        return tickets.stream()
                .map(Ticket::getPrice)
                .min(Integer::compareTo)
                .orElse(-1);
    }

    public double findPriceDifferenceBetweenAveAndMed(String origin, String destination) {
        List<Ticket> allTickets = ticketRepository.findByRoute(origin, destination);
        if (allTickets.size() == 0) {
            return -1;
        }
        if (allTickets.size() == 1) {
            return 0;
        }
        List<Integer> prices = allTickets.stream()
                .map(Ticket::getPrice)
                .sorted()
                .toList();

        double medianPrice;
        if (prices.size() % 2 == 0) {
            medianPrice = (double) (prices.get(prices.size() / 2) + prices.get(prices.size() / 2 - 1)) / 2;
        } else {
            medianPrice = prices.get(prices.size() / 2);
        }
        double averagePrice = 0;
        for (Integer it : prices) {
            averagePrice += it;
        }
        return averagePrice / prices.size() - medianPrice;
    }
}