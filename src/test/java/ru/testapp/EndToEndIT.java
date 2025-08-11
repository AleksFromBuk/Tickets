package ru.testapp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static junit.framework.Assert.assertTrue;

public class EndToEndIT {
    @Test
    //@Disabled
    void endToEnd_runsAndWritesOutput() throws Exception {
        Path resource = Path.of("src/test/resources/tickets-test.json");
        Path out = Files.createTempFile("out", ".txt");
        Application.main(new String[] {"--path", resource.toString(), "--out", out.toString(), "--from", "VVO", "--to", "TLV"});
        String s = Files.readString(out);
        String expected = "Минимальное время полета";
        assertTrue(s.contains(expected));
        assertTrue(s.contains("Разница между средней ценой и медианой"));
    }

    @Test
    void endToEnd_runsAndWritesOutput(@TempDir Path tmp) throws Exception {
        Path resource = tmp.resolve("tickets-small.json");
        String json = """
                {
                  "tickets":[
                    {
                      "origin":"VVO",
                      "origin_name":"Владивосток",
                      "destination":"TLV",
                      "destination_name":"Тель-Авив",
                      "departure_date":"12.05.18",
                      "departure_time":"06:10",
                      "arrival_date":"12.05.18",
                      "arrival_time":"15:25",
                      "carrier":"TK",
                      "stops":0,
                      "price":14250
                    }
                  ]
                }
                """;
        Files.writeString(resource, json, StandardCharsets.UTF_8);

        Path out = tmp.resolve("result.txt");
        Application.main(new String[] {"--path", resource.toString(), "--out", out.toString(), "--from", "VVO", "--to", "TLV"});

        String s = Files.readString(out);
        String expected = "Минимальное время полета";
        assertTrue(s.contains(expected));
        assertTrue(s.contains("Разница между средней ценой и медианой"));
    }
}
