package ru.testapp;

import lombok.extern.slf4j.Slf4j;
import ru.testapp.calculation.MinFlightTimeStrategy;
import ru.testapp.calculation.PriceDifferenceStrategy;
import ru.testapp.cli.Args;
import ru.testapp.cli.CommandLineParser;
import ru.testapp.exception.ApplicationException;
import ru.testapp.exception.ValidationException;
import ru.testapp.output.ResultFormatter;
import ru.testapp.output.ResultWriter;
import ru.testapp.repository.JsonTicketRepository;
import ru.testapp.repository.TicketRepository;
import ru.testapp.service.AnalysisService;
import ru.testapp.service.CalculationType;

import java.time.Duration;
import java.util.Map;

/**
 * Точка входа приложения.
 */
@Slf4j
public class Application {
    public static void main(String[] args) {
        try {
            Args parsed = CommandLineParser.parse(args);
            log.info("Starting with parameters: {}", parsed);

            TicketRepository repo = new JsonTicketRepository(parsed.getPath(), parsed.isCache());
            AnalysisService service = new AnalysisService(repo);

            service.register(CalculationType.MIN_FLIGHT_TIME,
                    new MinFlightTimeStrategy(parsed.getOrigin(), parsed.getDestination()));
            service.register(CalculationType.PRICE_DIFFERENCE,
                    new PriceDifferenceStrategy(parsed.getOrigin(), parsed.getDestination()));
            Map<String, Object> results = service.calculateAll(parsed.getOrigin(), parsed.getDestination());
            ResultFormatter formatter = new ResultFormatter();
            StringBuilder output = new StringBuilder();

            @SuppressWarnings("unchecked")
            Map<String, Duration> minTimes = (Map<String, Duration>) results.get(CalculationType.MIN_FLIGHT_TIME);
            output.append(formatter.formatMinFlightTimes(minTimes, parsed.getOrigin(), parsed.getDestination()));
            output.append(System.lineSeparator());

            double diff = (Double) results.get(CalculationType.PRICE_DIFFERENCE);
            output.append(formatter.formatPriceDifference(diff));

            new ResultWriter().write(output.toString(), parsed.getOut());
            System.out.println(output.toString());
        } catch (ValidationException ve) {
            log.error("Validation error", ve);
            System.err.println(ve.getMessage());
            System.exit(2);
        } catch (ApplicationException ae) {
            log.error("Application error", ae);
            System.err.println(ae.getMessage());
            System.exit(ae.getExitCode());
        } catch (Exception e) {
            log.error("Unexpected error", e);
            System.err.println("Unexpected error: " + e.getMessage());
            System.exit(1);
        }
    }
}
