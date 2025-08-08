package ru.testapp.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import ru.testapp.util.DateUtils;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Immutable representation of a ticket.
 * Jackson создаёт объект через конструктор {@link com.fasterxml.jackson.annotation.JsonCreator}
 */
@Getter
public final class Ticket {
    private final String origin;
    private final String originName;
    private final String destination;
    private final String destinationName;
    private final String departureDateStr;
    private final String departureTimeStr;
    private final String arrivalDateStr;
    private final String arrivalTimeStr;
    private final String carrier;
    private final int stops;
    private final int price;

    // Поля parsed
    private final LocalDate departureDate;
    private final LocalTime departureTime;
    private final LocalDate arrivalDate;
    private final LocalTime arrivalTime;

    @JsonCreator
    public Ticket(
            @JsonProperty("origin") String origin,
            @JsonProperty("origin_name") String originName,
            @JsonProperty("destination") String destination,
            @JsonProperty("destination_name") String destinationName,
            @JsonProperty("departure_date") String departureDateStr,
            @JsonProperty("departure_time") String departureTimeStr,
            @JsonProperty("arrival_date") String arrivalDateStr,
            @JsonProperty("arrival_time") String arrivalTimeStr,
            @JsonProperty("carrier") String carrier,
            @JsonProperty("stops") int stops,
            @JsonProperty("price") int price) {
        this.origin = origin;
        this.originName = originName;
        this.destination = destination;
        this.destinationName = destinationName;
        this.departureDateStr = departureDateStr;
        this.departureTimeStr = departureTimeStr;
        this.arrivalDateStr = arrivalDateStr;
        this.arrivalTimeStr = arrivalTimeStr;
        this.carrier = carrier;
        this.stops = stops;
        this.price = price;

        // парсим строки в LocalDate/LocalTime с валидацией
        this.departureDate = DateUtils.parseDateStrict(departureDateStr);
        this.departureTime = DateUtils.parseTimeStrict(departureTimeStr);
        this.arrivalDate = DateUtils.parseDateStrict(arrivalDateStr);
        this.arrivalTime = DateUtils.parseTimeStrict(arrivalTimeStr);
    }
}
