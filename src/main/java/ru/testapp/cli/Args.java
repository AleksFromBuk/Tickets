package ru.testapp.cli;

import lombok.Getter;
import lombok.ToString;

import java.nio.file.Path;

/**
 * DTO для CLI-аргументов.
 */
@Getter
@ToString
public class Args {
    private final Path path;
    private final Path out;
    private final String origin;
    private final String destination;
    private final boolean cache;

    public Args(Path path, Path out, String origin, String destination, boolean cache) {
        this.path = path;
        this.out = out;
        this.origin = origin;
        this.destination = destination;
        this.cache = cache;
    }
}
