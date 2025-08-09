package ru.testapp.calculation;

import org.junit.jupiter.api.Test;
import ru.testapp.domain.Ticket;

import java.util.stream.Stream;

import static junit.framework.Assert.assertEquals;
import static org.assertj.core.api.Assertions.*;

class PriceDifferenceStrategyTest {
    @Test
    void medianAndAverage_calculation_evenAndOdd() {
        PriceDifferenceStrategy strat = new PriceDifferenceStrategy("VVO","TLV");

        Ticket t1 = new Ticket("VVO","A","TLV","B","12.05.18","10:00","12.05.18","14:00","X",0,100);
        Ticket t2 = new Ticket("VVO","A","TLV","B","12.05.18","10:00","12.05.18","14:00","X",0,200);
        Ticket t3 = new Ticket("VVO","A","TLV","B","12.05.18","10:00","12.05.18","14:00","X",0,300);
        double resOdd = strat.calculate(Stream.of(t1,t2,t3));
        // avg = 200, median = 200 => diff 0
        assertEquals(0.0, resOdd, 1e-6);

        double resEven = strat.calculate(Stream.of(
                t1,t2,t3,
                new Ticket("VVO","A","TLV","B","12.05.18","10:00","12.05.18","14:00","X",0,400)));
        // avg = 250, median = (200+300)/2 = 250 => diff 0
        assertEquals(0.0, resEven, 1e-6);
    }

}