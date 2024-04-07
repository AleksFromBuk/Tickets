package ru.testapp.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.testapp.Ticket;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JsonTicketRepository implements TicketRepository {
    private final String jsonFilePath;

    public JsonTicketRepository(String jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
    }

    @Override
    public List<Ticket> findAll() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonTickets = objectMapper.readTree(new FileReader(jsonFilePath, StandardCharsets.UTF_8)).get("tickets");
            return objectMapper.readValue(jsonTickets.toString(), new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON file", e);
        }
    }

    @Override
    public List<Ticket> findByRoute(String origin, String destination) {
        return findAll()
                .stream()
                .filter(t -> t.getOrigin().equals(origin) && t.getDestination().equals(destination))
                .toList();
    }
}
