package ru.testapp.calculation;

import org.junit.jupiter.api.Test;
import ru.testapp.domain.Ticket;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Stream;

import static junit.framework.Assert.assertEquals;

class MinFlightTimeStrategyTest {
    @Test
    void calculatesMinFlightTimes() {
        MinFlightTimeStrategy strat = new MinFlightTimeStrategy();

        Ticket t1 = new Ticket("VVO","A","TLV","B","12.05.18","10:00","12.05.18","14:00","TK",0,100);
        Ticket t2 = new Ticket("VVO","A","TLV","B","12.05.18","11:00","12.05.18","15:30","TK",0,150);
        Ticket t3 = new Ticket("VVO","A","TLV","B","12.05.18","12:00","12.05.18","18:00","SU",0,200);

        Map<String, Duration> res = strat.calculate(Stream.of(t1, t2, t3));

        // физические durations (с учётом зон): t1 = 11h, t2 = 11h30m, t3 = 13h
        assertEquals(Duration.ofHours(11), res.get("TK"));
        assertEquals(Duration.ofHours(13), res.get("SU"));
    }
}