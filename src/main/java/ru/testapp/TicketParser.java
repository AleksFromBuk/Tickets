package ru.testapp;

import ru.testapp.repository.JsonTicketRepository;
import ru.testapp.repository.TicketRepository;
import ru.testapp.util.ArgsName;
import ru.testapp.util.PrintResult;

import java.util.Map;

public record TicketParser(TicketRepository ticketRepository, TicketService ticketService) {
    private static final String PATH = "path";
    private static final String DEFAULT_SOURCE = "Tickets.json";
    private static final String DEFAULT_OUTPUT = "result.txt";
    private static final String OUT = "out";
    private static final String FILTER = "filter";
    private static final String ORIGIN = "VVO";
    private static final String DESTINATION = "TLV";

    public static void main(String[] args) {
        ArgsName argsName = ArgsName.of(args);
        String pathSourceFile = argsName.getOrDefault(PATH, DEFAULT_SOURCE);
        String output = argsName.getOrDefault(OUT, DEFAULT_OUTPUT);
        String[] route = argsName.getOrDefault(FILTER, ORIGIN + "," + DESTINATION).split(",");
        TicketRepository ticketRepository = new JsonTicketRepository(pathSourceFile);
        TicketParser parser = new TicketParser(ticketRepository, new TicketService(ticketRepository));
        double requiredDiff = parser.ticketService.findPriceDifferenceBetweenAveAndMed(route[0], route[1]);
        Map<String, Long> mapResult = parser.ticketService.minFlightTimeOnTheRoute(route[0], route[1]);
        PrintResult.printResult(mapResult, requiredDiff, output);
    }
}