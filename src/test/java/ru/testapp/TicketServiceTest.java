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
import java.util.Map;
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
    void findMinPricesForAllCarrierByLRNAndTLV() {
        List<Ticket> tickets = ticketRepository.findAll();
        assertThat(tickets.size()).isEqualTo(9);
        List<Ticket> ticketByRouteLRNAndTLV = ticketRepository.findByRoute("LRN", "TLV");
        assertThat(ticketByRouteLRNAndTLV.size()).isEqualTo(8);
        Map<String, Integer> map = ticketService.minPricesOfCarriersOnTheRoute("LRN", "TLV");
        assertThat(map.size()).isEqualTo(4);
        assertThat(map.get("S7")).isEqualTo(-2);
        assertThat(map.get("BA")).isEqualTo(-1);
        assertThat(map.get("SU")).isEqualTo(-3);
        assertThat(map.get("TK")).isEqualTo(-4);
    }

    @Test
    void findPriceDifferenceBetweenAveAndMed() {
        List<Ticket> tickets = ticketRepository.findAll();
        assertThat(tickets.size()).isEqualTo(9);
        List<Ticket> ticketByRouteLRNAndTLV = ticketRepository.findByRoute("LRN", "TLV");
        assertThat(ticketByRouteLRNAndTLV.size()).isEqualTo(8);
        double averagePrice = ticketByRouteLRNAndTLV
                .stream()
                .mapToInt(Ticket::getPrice)
                .average().orElse(0);
        List<Integer> tmpList =  ticketByRouteLRNAndTLV
                .stream()
                .map(Ticket::getPrice)
                .sorted()
                .toList();
        double medianPrice = (double) (tmpList.get(3) + tmpList.get(4)) / 2;
        double expectedValue = averagePrice - medianPrice;
        double actualValue = ticketService.findPriceDifferenceBetweenAveAndMed("LRN", "TLV");
        assertThat(Double.compare(actualValue, expectedValue)).isEqualTo(0);
    }

    @Test
    void whenNoDataForSelectedRoute() throws IOException {
        String[] inputData = new String[]{
                "-path=" + path,
                "-out=" + outputPath, "-filter=AAA,BBB"
        };
        String expected =
                "there is no data for calculations in the selected direction"
                        + System.lineSeparator();
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
        String expected = String.join(
                        System.lineSeparator(),
                        "the minimum price of the BA carrier for the selected route is:  -1",
                        "the minimum price of the S7 carrier for the selected route is:  -2",
                        "the minimum price of the SU carrier for the selected route is:  -3",
                        "the minimum price of the TK carrier for the selected route is:  -4",
                        "the difference between the average price and the median is: 2136,75000")
                .concat(System.lineSeparator());
        TicketParser.main(inputData);
        assertThat(Files.readString(outputPath, StandardCharsets.UTF_8)).isEqualTo(expected);
        try (FileChannel channel = FileChannel.open(outputPath, StandardOpenOption.WRITE)) {
            channel.truncate(0);
        }
    }
}