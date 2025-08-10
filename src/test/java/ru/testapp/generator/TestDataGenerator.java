package ru.testapp.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Генератор тестовых JSON-файлов с билетами.
 */
public final class TestDataGenerator {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yy");

    public static Path generateLargeFile(Path targetFile, int numRecords) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        ArrayNode arr = root.putArray("tickets");
        Random rnd = new Random(42);

        LocalDate base = LocalDate.of(2018, 5, 12);
        String[] carriers = {"TK", "SU", "S7", "BA", "LH"};
        String[] origins = {"VVO", "VVO", "VVO", "VVO", "LRN"};
        String[] dests = {"TLV", "TLV", "TLV", "TLV", "TLV"};
        DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yy");

        for (int i = 0; i < numRecords; i++) {
            ObjectNode t = arr.addObject();
            int idx = rnd.nextInt(carriers.length);
            String origin = origins[idx];
            String dest = dests[idx];

            // Гарантируем, что дата прибытия не раньше даты вылета
            int departureDayOffset = rnd.nextInt(5);
            LocalDate departureDate = base.plusDays(departureDayOffset);

            // Прибытие в тот же день или позже (мин. +0, макс. +5 дней)
            int maxDaysAfter = 5;
            int daysAfter = rnd.nextInt(maxDaysAfter + 1); // 0-5 дней
            LocalDate arrivalDate = departureDate.plusDays(daysAfter);

            // Гарантируем, что время прибытия позже времени вылета для билетов в один день
            int departureHour = 6 + rnd.nextInt(12); // 06:00-17:00
            int departureMinute = rnd.nextInt(60);

            int arrivalHour;
            if (daysAfter == 0) {
                // Для рейсов в один день - прибытие минимум через 1 час
                arrivalHour = departureHour + 1 + rnd.nextInt(10);
                if (arrivalHour >= 24) arrivalHour = 23;
            } else {
                arrivalHour = 8 + rnd.nextInt(14); // 08:00-21:00
            }
            int arrivalMinute = rnd.nextInt(60);

            t.put("origin", origin);
            t.put("origin_name", "O" + i);
            t.put("destination", dest);
            t.put("destination_name", "D" + i);
            t.put("departure_date", DATE.format(departureDate));
            t.put("departure_time", String.format("%02d:%02d", departureHour, departureMinute));
            t.put("arrival_date", DATE.format(arrivalDate));
            t.put("arrival_time", String.format("%02d:%02d", arrivalHour, arrivalMinute));
            t.put("carrier", carriers[rnd.nextInt(carriers.length)]);
            t.put("stops", rnd.nextInt(4));
            t.put("price", 10000 + rnd.nextInt(20000));
        }

        mapper.writerWithDefaultPrettyPrinter().writeValue(targetFile.toFile(), root);
        return targetFile;
    }


    //**************  версия 2 ******************
//    public static Path generateLargeFile(Path targetFile, int numRecords) throws IOException {
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectNode root = mapper.createObjectNode();
//        ArrayNode arr = root.putArray("tickets");
//        Random rnd = new Random(42);
//
//        LocalDate base = LocalDate.of(2018,5,12);
//        String[] carriers = {"TK","SU","S7","BA","LH"};
//        String[] origins = {"VVO","VVO","VVO","VVO","LRN"};
//        String[] dests = {"TLV","TLV","TLV","TLV","TLV"};
//
//        for (int i = 0; i < numRecords; i++) {
//            ObjectNode t = arr.addObject();
//            int idx = rnd.nextInt(carriers.length);
//            String origin = origins[idx];
//            String dest = dests[idx];
//
//            // Генерация дат с гарантией, что прибытие не раньше вылета
//            int departureDayOffset = rnd.nextInt(5);
//            LocalDate departureDate = base.plusDays(departureDayOffset);
//
//            // Прибытие в тот же день или позже
//            int maxArrivalDayOffset = departureDayOffset + 5; // Максимум +5 дней
//            int arrivalDayOffset = departureDayOffset + rnd.nextInt(maxArrivalDayOffset - departureDayOffset + 1);
//            LocalDate arrivalDate = base.plusDays(arrivalDayOffset);
//
//            t.put("origin", origin);
//            t.put("origin_name", "O" + i);
//            t.put("destination", dest);
//            t.put("destination_name", "D" + i);
//            t.put("departure_date", DATE.format(departureDate));
//            t.put("departure_time", String.format("%02d:%02d", 6 + rnd.nextInt(12), rnd.nextInt(60)));
//            t.put("arrival_date", DATE.format(arrivalDate));
//            t.put("arrival_time", String.format("%02d:%02d", 8 + rnd.nextInt(14), rnd.nextInt(60)));
//            t.put("carrier", carriers[rnd.nextInt(carriers.length)]);
//            t.put("stops", rnd.nextInt(4));
//            t.put("price", 10000 + rnd.nextInt(20000));
//        }
//        mapper.writerWithDefaultPrettyPrinter().writeValue(targetFile.toFile(), root);
//        return targetFile;
//    }


    //*********** версия 1 ******************
//    public static Path generateLargeFile(Path targetFile, int numRecords) throws IOException {
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectNode root = mapper.createObjectNode();
//        ArrayNode arr = root.putArray("tickets");
//        Random rnd = new Random(42);
//
//        LocalDate base = LocalDate.of(2018,5,12);
//        String[] carriers = {"TK","SU","S7","BA","LH"};
//        String[] origins = {"VVO","VVO","VVO","VVO","LRN"};
//        String[] dests = {"TLV","TLV","TLV","TLV","TLV"};
//
//        for (int i = 0; i < numRecords; i++) {
//            ObjectNode t = arr.addObject();
//            int idx = rnd.nextInt(carriers.length);
//            String origin = origins[idx];
//            String dest = dests[idx];
//            t.put("origin", origin);
//            t.put("origin_name", "O" + i);
//            t.put("destination", dest);
//            t.put("destination_name", "D" + i);
//            t.put("departure_date", DATE.format(base.plusDays(rnd.nextInt(5))));
//            t.put("departure_time", String.format("%02d:%02d", 6 + rnd.nextInt(12), rnd.nextInt(60)));
//            t.put("arrival_date", DATE.format(base.plusDays(rnd.nextInt(10))));
//            t.put("arrival_time", String.format("%02d:%02d", 8 + rnd.nextInt(14), rnd.nextInt(60)));
//            t.put("carrier", carriers[rnd.nextInt(carriers.length)]);
//            t.put("stops", rnd.nextInt(4));
//            t.put("price", 10000 + rnd.nextInt(20000));
//        }
//        mapper.writerWithDefaultPrettyPrinter().writeValue(targetFile.toFile(), root);
//        return targetFile;
//    }
}
