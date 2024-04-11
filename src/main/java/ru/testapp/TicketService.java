package ru.testapp;

import ru.testapp.repository.TicketRepository;
import ru.testapp.util.TimesInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

    public Map<String, Integer> minPricesOfCarriersOnTheRoute(String origin, String destination) {
        Map<String, Integer> map = new TreeMap<>();
        String tmpCarrier;
        int tmpMinPrice;
        for (Ticket obj : ticketRepository.findByRoute(origin, destination)) {
            tmpMinPrice = obj.getPrice();
            tmpCarrier = obj.getCarrier();
            if (!map.containsKey(tmpCarrier) || map.get(tmpCarrier) > obj.getPrice()) {
                map.put(tmpCarrier, tmpMinPrice);
            }
        }
        return map;
    }

    public Map<String, Long> minFlightTimeOnTheRoute(String origin, String destination) {
        Map<String, Long> map = new TreeMap<>();
        String tmpCarrier;
        for (Ticket obj : ticketRepository.findByRoute(origin, destination)) {
            tmpCarrier = obj.getCarrier();
            long itLongValue = TimesInfo.getDataTime(obj.getDepartureDate(), obj.getDepartureTime(),
                    obj.getArrivalDate(), obj.getArrivalTime());
            if (!map.containsKey(tmpCarrier) || map.get(tmpCarrier) > itLongValue) {
                map.put(tmpCarrier, itLongValue);
            }
        }
        return map;
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
        double sum = 0;
        for (Integer it : prices) {
            sum += it;
        }
        return sum / prices.size() - medianPrice;
    }
}