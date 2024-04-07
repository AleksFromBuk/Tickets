package ru.testapp.util;

import java.util.HashMap;
import java.util.Map;

public class ArgsName {
    private final Map<String, String> values = new HashMap<>();

    public String get(String key) {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException("This key: '" + key + "' is missing");
        }
        return values.get(key);
    }

    private void parse(String[] args) {
        for (String it : args) {
            String[] tmpIt = it.substring(1).split("=", 2);
            values.put(tmpIt[0], tmpIt[1]);
        }
    }

    private static void dataValidation(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("Arguments not passed to program");
        }
        for (String it : args) {
            if (!it.startsWith("-")) {
                throw new IllegalArgumentException("Error: This argument '" + it + "' does not start with a '-' character");
            }
            if (!it.contains("=")) {
                throw new IllegalArgumentException("Error: This argument '" + it + "' does not contain an equal sign");
            }
            String tmp = it.substring(1);
            String[] tmpIt = tmp.split("=", 2);
            if (tmpIt[0].isBlank()) {
                throw new IllegalArgumentException("Error: This argument '" + it + "' does not contain a key");
            }
            if (tmpIt[1].isBlank()) {
                throw new IllegalArgumentException("Error: This argument '" + it + "' does not contain a value");
            }
        }
    }

    public static ArgsName of(String[] args) {
        dataValidation(args);
        ArgsName names = new ArgsName();
        names.parse(args);
        return names;
    }
}
