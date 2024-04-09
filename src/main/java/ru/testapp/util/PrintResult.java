package ru.testapp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.testapp.TicketParser;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class PrintResult {
    private static final Logger LOG = LoggerFactory.getLogger(TicketParser.class.getName());

    public static void printResult(Map<String, Integer> map, double requiredDiff, String outputPath) {
        try (var writer = new PrintWriter(new FileWriter(outputPath, StandardCharsets.UTF_8, true))) {
            if (map.isEmpty()) {
                writer.println("there is no data for calculations in the selected direction");
            } else {
                map.forEach((key, value) -> writer.printf("the minimum price of the %s carrier for the selected route is:  %d" + System.lineSeparator(),
                        key, value));

                writer.printf("the difference between the average price and the median is: %.5f" + System.lineSeparator(), requiredDiff);
            }
        } catch (IOException e) {
            LOG.error("I/O error", e);
        }
    }
}
