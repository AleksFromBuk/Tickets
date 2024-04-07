package ru.testapp;

import org.junit.jupiter.api.Test;
import ru.testapp.repository.JsonTicketRepository;
import ru.testapp.repository.TicketRepository;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;

import static java.nio.file.Paths.get;
import static org.assertj.core.api.Assertions.assertThat;

class TicketServiceTest {
    Path path = get(Objects.requireNonNull(getClass().getClassLoader().getResource("tickets.json")).toURI());
    Path outputPath = get(Objects.requireNonNull(getClass().getClassLoader().getResource("output.txt")).toURI());
    TicketRepository ticketRepository = new JsonTicketRepository(path.toString());
    TicketService ticketService = new TicketService(ticketRepository);

    TicketServiceTest() throws URISyntaxException {
    }

    @Test
    void findMinPriceForTargetDirection() {
        List<Ticket> tickets = ticketRepository.findAll();
        assertThat(tickets.size()).isEqualTo(5);
        List<Ticket> ticketByRouteVVOAndTLV = ticketRepository.findByRoute("VVO", "TLV");
        assertThat(ticketByRouteVVOAndTLV.size()).isEqualTo(2);
        int minPriceExpected = Math.min(ticketByRouteVVOAndTLV.get(0).getPrice(), ticketByRouteVVOAndTLV.get(1).getPrice());
        int minPriceActual = ticketService.findMinPriceForTargetDirection("VVO", "TLV");
        assertThat(minPriceExpected).isEqualTo(minPriceActual);
    }

    @Test
    void findPriceDifferenceBetweenAveAndMed() {
        List<Ticket> tickets = ticketRepository.findAll();
        assertThat(tickets.size()).isEqualTo(5);
        List<Ticket> ticketByRouteVVOAndTLV = ticketRepository.findByRoute("VVO", "TLV");
        assertThat(ticketByRouteVVOAndTLV.size()).isEqualTo(2);
        double averagePrice = (double) (ticketByRouteVVOAndTLV.get(0).getPrice() + ticketByRouteVVOAndTLV.get(1).getPrice()) / 2;
        double medianPrice = (double) (ticketByRouteVVOAndTLV.get(0).getPrice() + ticketByRouteVVOAndTLV.get(1).getPrice()) / 2;
        double expectedValue = averagePrice - medianPrice;
        double actualValue = ticketService.findPriceDifferenceBetweenAveAndMed("VVO", "TLV");
        assertThat(Double.compare(actualValue, expectedValue)).isEqualTo(0);
    }

    @Test
    void whenNoDataForSelectedRoute() throws IOException {
        String[] inputData = new String[]{
                "-path=" + path,
                "-out=" + outputPath, "-filter=VVOa,TLV"
        };
        String tmp =
                "there is no data for calculations in the selected direction"
                        + System.lineSeparator();
        String expected = new String(tmp.getBytes(), StandardCharsets.UTF_8);
        TicketParser.main(inputData);
        assertThat(Files.readString(outputPath, StandardCharsets.UTF_8)).isEqualTo(expected);
        try (FileChannel channel = FileChannel.open(outputPath, StandardOpenOption.WRITE)) {
            channel.truncate(0);
        }
    }

    @Test
    void whenWeHaveDataForSelectedRoute() throws IOException {
        String[] inputData = new String[]{
                "-path=" + path,
                "-out=" + outputPath, "-filter=LRN,TLV"
        };
        String tmp =
                "minimum price for the selected direction: 4000"
                        + System.lineSeparator()
                        + "the difference between the average price and the median is: -333,33333"
                        + System.lineSeparator();
        String expected = new String(tmp.getBytes(), StandardCharsets.UTF_8);
        TicketParser.main(inputData);
        assertThat(Files.readString(outputPath, StandardCharsets.UTF_8)).isEqualTo(expected);
        try (FileChannel channel = FileChannel.open(outputPath, StandardOpenOption.WRITE)) {
            channel.truncate(0);
        }
    }
}