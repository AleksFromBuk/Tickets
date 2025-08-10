package ru.testapp.cli;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

class CommandLineParserTest {
    @Test
    void parse_defaultsAndFlags() {
        Args a = CommandLineParser.parse(new String[] {});
        assertEquals("VVO", a.getOrigin());
        assertEquals("TLV", a.getDestination());

        String tmp = "src/test/resources/tickets-test.json";
        Args b = CommandLineParser.parse(new String[] {"-p", tmp, "-o", "out.txt", "-f", "VVO", "-t", "TLV", "--cache"});
        assertEquals(Path.of(tmp), b.getPath());
        assertTrue(b.isCache());
    }
}