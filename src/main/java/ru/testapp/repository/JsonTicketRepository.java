package ru.testapp.repository;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ru.testapp.domain.Ticket;
import ru.testapp.exception.DataLoadException;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
public class JsonTicketRepository implements TicketRepository {
    private final Path file;
    private final ObjectMapper mapper;
    private final boolean cacheEnabled;
    private final long cacheSizeThresholdBytes = 5 * 1024 * 1024L;
    private static final String TICKETS_JSON_NODE_ROOT = "tickets";
    private volatile List<Ticket> cachedTickets;

    public JsonTicketRepository(Path file, boolean cacheEnabled) {
        this.file = file;
        this.cacheEnabled = cacheEnabled;
        mapper = new ObjectMapper();
    }

    @Override
    public Stream<Ticket> streamTickets() {
        if (cacheEnabled && shouldUseCache()) {
            return getCachedTickets().stream();
        }
        return streamTicketsFromParser();
    }

    private boolean shouldUseCache() {
        try {
            return Files.size(file) <= cacheSizeThresholdBytes;
        } catch (Exception e) {
            log.warn("Error checking file size", e);
            return false;
        }
    }

    private List<Ticket> getCachedTickets() {
        if (cachedTickets == null) {
            synchronized (this) {
                if (cachedTickets == null) {
                    try (InputStream is = Files.newInputStream(file)) {
                        JsonNode root = mapper.readTree(is);
                        JsonNode ticketsNode = root.get(TICKETS_JSON_NODE_ROOT);
                        if (ticketsNode == null || !ticketsNode.isArray()) {
                            throw new DataLoadException("Missing 'tickets' array in JSON", 3);
                        }
                        List<Ticket> tickets = new ArrayList<>();
                        for (JsonNode node : ticketsNode) {
                            tickets.add(mapper.treeToValue(node, Ticket.class));
                        }
                        cachedTickets = Collections.unmodifiableList(tickets);
                    } catch (Exception e) {
                        throw new DataLoadException("Cannot cache JSON file: " + file, e, 3);
                    }
                }
            }
        }
        return cachedTickets;
    }

    private Stream<Ticket> streamTicketsFromParser() {
        try {
            InputStream is = Files.newInputStream(file);
            JsonFactory factory = mapper.getFactory();
            JsonParser parser = factory.createParser(is);

            JsonToken token;
            boolean found = false;
            while ((token = parser.nextToken()) != null) {
                if (token == JsonToken.FIELD_NAME && TICKETS_JSON_NODE_ROOT.equals(parser.getCurrentName())) {
                    parser.nextToken();
                    found = true;
                    break;
                }
            }
            if (!found || parser.currentToken() != JsonToken.START_ARRAY) {
                parser.close();
                is.close();
                throw new DataLoadException("Missing 'tickets' array in JSON", 3);
            }

            JsonTicketIterator iterator = new JsonTicketIterator(parser, mapper);
            Spliterator<Ticket> spliterator = Spliterators
                    .spliteratorUnknownSize(iterator, Spliterator.ORDERED);
            return StreamSupport.stream(spliterator, false).onClose(() -> {
                try {
                    parser.close();
                    is.close();
                } catch (Exception e) {
                    log.warn("Error closing parser", e);
                }
            });
        } catch (DataLoadException dataLoadException) {
            throw dataLoadException;
        } catch (Exception e) {
            throw new DataLoadException("Cannot read JSON file: " + file, e, 3);
        }
    }
}
