package ru.testapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testapp.repository.JsonTicketRepository;
import ru.testapp.repository.TicketRepository;
import ru.testapp.util.ArgsName;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TicketParser {
    private static final Logger LOG = LoggerFactory.getLogger(TicketParser.class.getName());
    private static final String PATH = "path";
    private static final String DEFAULT_SOURCE = "Tickets.json";
    private static final String DEFAULT_OUTPUT = "result.txt";
    private static final String OUT = "out";
    private static final String FILTER = "filter";
    private static final String ORIGIN = "VVO";
    private static final String DESTINATION = "TLV";
    private static ArgsName argsName;

    public static void main(String[] args) {
        String pathSourceFile;
        if (args.length == 0) {
            pathSourceFile = DEFAULT_SOURCE;
        } else {
            argsName = validateParams(args);
            pathSourceFile = argsName.get(PATH);
        }
        TicketRepository ticketRepository = new JsonTicketRepository(pathSourceFile);
        TicketService ticketService = new TicketService(ticketRepository);
        int minPrice;
        double requiredDiff;
        String output = DEFAULT_OUTPUT;
        if (args.length == 0) {
            minPrice = ticketService.findMinPriceForTargetDirection(ORIGIN, DESTINATION);
            requiredDiff = ticketService.findPriceDifferenceBetweenAveAndMed(ORIGIN, DESTINATION);
        } else {
            String[] route = argsName.get(FILTER).split(",");
            minPrice = ticketService.findMinPriceForTargetDirection(route[0], route[1]);
            requiredDiff = ticketService.findPriceDifferenceBetweenAveAndMed(route[0], route[1]);
            output = argsName.get(OUT);
        }
        printResult(minPrice, requiredDiff, output);
    }

    private static void printResult(int minPrice, double requiredDiff, String outputPath) {
        try (var writer = new PrintWriter(new FileWriter(outputPath, StandardCharsets.UTF_8, true))) {
            if (minPrice == -1 || Double.compare(requiredDiff, -1) == 0) {
                writer.println("there is no data for calculations in the selected direction");
            } else {
                writer.printf("minimum price for the selected direction: %d" + System.lineSeparator()
                        + "the difference between the average price and the median is: %.5f" + System.lineSeparator(), minPrice, requiredDiff);
            }
        } catch (IOException e) {
            LOG.error("I/O error", e);
        }
    }

    private static ArgsName validateParams(String[] args) {
        final ArgsName argsName = ArgsName.of(args);
        String sourcePath = argsName.get(PATH);
        String outputPath = argsName.get(OUT);
        if (!(Files.exists(Path.of(sourcePath)))) {
            throw new IllegalArgumentException("the selected folder " + sourcePath + " does not exist");
        }
        if (!(Files.exists(Path.of(outputPath)))) {
            throw new IllegalArgumentException("the selected folder " + outputPath + " does not exist");
        }
        return argsName;
    }
}