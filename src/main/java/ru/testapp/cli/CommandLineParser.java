package ru.testapp.cli;

import ru.testapp.exception.ValidationException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Простой парсер CLI флагов.
 * Поддерживает: -p/--path, -o/--out, -f/--from, -t/--to, --cache (опция).
 */
public class CommandLineParser {
    private static final String DEFAULT_INPUT = "tickets.json";
    private static final String DEFAULT_OUTPUT = "result.text";
    private static final String DEFAULT_FROM = "VVO";
    private static final String DEFAULT_TO = "TLV";

    /**
     * Парсит аргументы и валидирует.
     *
     * @param args аргументы командной строки
     * @return объект Args
     * @throws ValidationException если аргументы неверны
     */
    public static Args parse(String[] args) {
        Map<CommonParam, String> map = new HashMap<>();
        map.put(CommonParam.PATH, DEFAULT_INPUT);
        map.put(CommonParam.OUT, DEFAULT_OUTPUT);
        map.put(CommonParam.FROM, DEFAULT_FROM);
        map.put(CommonParam.TO, DEFAULT_TO);
        boolean cache = false;

        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            switch (a) {
                case "-p", "--path" -> map.put(CommonParam.PATH, value(args, ++i, a));
                case "-o", "--out" -> map.put(CommonParam.OUT, value(args, ++i, a));
                case "-f", "--from" -> map.put(CommonParam.FROM, value(args, ++i, a));
                case "-t", "-too" -> map.put(CommonParam.TO, value(args, ++i, a));
                case "--cache" -> cache = true;
                default -> {
                    // ignore unknown params
                }
            }
        }
        Path path = Paths.get(map.get(CommonParam.PATH));
        Path out = Paths.get(map.get(CommonParam.OUT));

        if (!Files.exists(path)) {
            throw new ValidationException("Входной файл не найден: " + path, 2);
        }
        // TODO странная реакция на данный кейс...
        if (Files.isDirectory((out))) {
            throw new ValidationException("ПУть вывода является директорией:  " + out, 3);
        }
        String origin = map.get(CommonParam.FROM).toUpperCase();
        String destination = map.get(CommonParam.TO).toUpperCase();
        if (origin.length() != 3 || destination.length() != 3) {
            throw new ValidationException("Коды аэропортов должны быть из 3 букв", 2);
        }

        return new Args(path, out, origin, destination, cache);
    }

    private static String value(String[] args, int idx, String flag) {
        if (idx >= args.length)
            throw new ValidationException("Отсутствует значение для " + flag, 2);
        return args[idx];
    }

}

