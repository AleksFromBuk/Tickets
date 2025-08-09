package ru.testapp.repository;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ru.testapp.domain.Ticket;
import ru.testapp.exception.DataLoadException;

import java.util.Iterator;

/**
 * Итератор, читающий объекты Ticket из JsonParser.
 */
@Slf4j
public class JsonTicketIterator implements Iterator<Ticket> {
    private final JsonParser parser;
    private final ObjectMapper mapper;
    private JsonToken nextToken;

    public JsonTicketIterator(JsonParser parser, ObjectMapper mapper) {
        this.parser = parser;
        this.mapper = mapper;
        try {
            this.nextToken = parser.nextToken(); //первый элемент или END_ARRAY
        } catch (Exception e) {
            throw new DataLoadException("Ошибка инициализации итератора JSON", e, 3);
        }
    }

    @Override
    public boolean hasNext() {
        return nextToken != null && nextToken != JsonToken.END_ARRAY;
    }

    @Override
    public Ticket next() {
        try {
            if (nextToken == JsonToken.START_OBJECT) {
                Ticket t = mapper.readValue(parser, Ticket.class);
                nextToken = parser.nextToken(); // advance
                return t;
            } else {
                throw new IllegalStateException("Ожидался START_OBJECT, но получен " + nextToken);
            }
        } catch (DataLoadException de) {
            throw de;
        } catch (Exception e) {
            log.warn("Пропуск ввода недействительного билета");
            // пропускаем повреждённую запись, пытаемся восстановиться
            try {
                JsonToken t;
                do {
                    t = parser.nextToken();
                } while (t != null && t != JsonToken.START_OBJECT && t != JsonToken.END_ARRAY);
                this.nextToken = t;
            } catch (Exception ex) {
                throw new DataLoadException("Ошибка при пропуске недействительного тикета", ex, 3);
            }
            if (nextToken == JsonToken.START_OBJECT) return next();
            throw new DataLoadException("Обнаружен некорректный тикет", e, 3);
        }
    }

}
