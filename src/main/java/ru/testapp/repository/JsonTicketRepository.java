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

/**
 * Streaming репозиторий. Параметр cacheEnabled включает попытку кэширования — файл читается полностью
 * и валидные записи сохраняются в cachedTickets. Любые некорректные записи при кэшировании будут пропущены.
 */
@Slf4j
public class JsonTicketRepository implements TicketRepository {
    private final Path file;
    private final ObjectMapper mapper;
    private final boolean cacheEnabled;
    private final long cacheSizeThresholdBytes; // порог (тест может изменить)
    private volatile List<Ticket> cachedTickets;
    private static final String TICKETS_NODE = "tickets";

    public JsonTicketRepository(Path file, boolean cacheEnabled, long cacheSizeThresholdBytes) {
        this.file = file;
        this.mapper = new ObjectMapper();
        this.cacheEnabled = cacheEnabled;
        this.cacheSizeThresholdBytes = cacheSizeThresholdBytes;
    }

    public JsonTicketRepository(Path file, boolean cacheEnabled) {
        this(file, cacheEnabled, 3 * 1024 * 1024L); // по умолчанию 3MB
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
            log.warn("Невозможно проверить размер файла, кэш не используется", e);
            return false;
        }
    }

    private List<Ticket> getCachedTickets() {
        if (cachedTickets == null) {
            synchronized (this) {
                if (cachedTickets == null) {
                    try (InputStream is = Files.newInputStream(file)) {
                        JsonNode root = mapper.readTree(is);
                        JsonNode ticketsNode = root.get(TICKETS_NODE);
                        if (ticketsNode == null || !ticketsNode.isArray()) {
                            log.warn("Отсутствует массив «tickets» в JSON");
                            throw new DataLoadException("Отсутствует массив «tickets» в JSON", 3);
                        }
                        List<Ticket> tickets = new ArrayList<>();
                        for (JsonNode node : ticketsNode) {
                            try {
                                Ticket t = mapper.treeToValue(node, Ticket.class);
                                tickets.add(t);
                            } catch (Exception ex) {
                                log.warn("Пропуск недействительного кэшированного билета: {}", ex.getMessage());
                            }
                        }
                        cachedTickets = Collections.unmodifiableList(tickets);
                    } catch (Exception e) {
                        log.warn("Невозможно кэшировать JSON-файл: " + file, e);
                        throw new DataLoadException("Невозможно кэшировать JSON-файл: " + file, e, 3);
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
                if (token == JsonToken.FIELD_NAME && TICKETS_NODE.equals(parser.getCurrentName())) {
                    parser.nextToken(); // перейти к START_ARRAY
                    found = true;
                    break;
                }
            }
            if (!found || parser.currentToken() != JsonToken.START_ARRAY) {
                parser.close();
                is.close();
                throw new DataLoadException("Отсутствует массив «tickets» в JSON", 3);
            }

            JsonTicketIterator iterator = new JsonTicketIterator(parser, mapper);
            Spliterator<Ticket> spliterator = Spliterators
                    .spliteratorUnknownSize(iterator, Spliterator.ORDERED);
            return StreamSupport.stream(spliterator, false).onClose(() -> {
                try {
                    parser.close();
                    is.close();
                } catch (Exception e) {
                    log.warn("Ошибка закрытия парсера", e);
                }
            });

        } catch (DataLoadException de) {
            throw de;
        } catch (Exception e) {
            log.warn("Невозможно прочитать файл JSON: " + file, e);
            throw new DataLoadException("Невозможно прочитать файл JSON: " + file, e, 3);
        }
    }
}
