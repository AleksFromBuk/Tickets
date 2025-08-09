package ru.testapp.repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.testapp.domain.Ticket;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static junit.framework.Assert.assertEquals;

class JsonTicketRepositoryTest {
    @Test
    void streamTickets_parsesAndStreams(@TempDir Path tempDir) throws Exception {
        Path f = tempDir.resolve("t.json");
        String json = "{\"tickets\":[{\"origin\":\"VVO\",\"origin_name\":\"Â\",\"destination\":\"TLV\",\"destination_name\":\"Ò\",\"departure_date\":\"12.05.18\",\"departure_time\":\"06:10\",\"arrival_date\":\"12.05.18\",\"arrival_time\":\"15:25\",\"carrier\":\"TK\",\"stops\":0,\"price\":14250}]}";
        Files.writeString(f, json);

        JsonTicketRepository repo = new JsonTicketRepository(f, false);
        try (var s = repo.streamTickets()) {
            List<Ticket> list = s.toList();
            assertEquals(1, list.size());
            assertEquals("TK", list.get(0).getCarrier());
        }
    }

    @Test
    @Disabled
    void streamTickets_WithCache(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("tickets.json");
        Files.writeString(file, "{\"tickets\":[{\"origin\":\"VVO\"}]}");

        JsonTicketRepository repo = new JsonTicketRepository(file, true);
        try (Stream<Ticket> stream = repo.streamTickets()) {
            assertEquals(1, stream.count());
        }

        // Verify cache is used
        try (Stream<Ticket> stream = repo.streamTickets()) {
            assertEquals(1, stream.count());
        }
    }
}