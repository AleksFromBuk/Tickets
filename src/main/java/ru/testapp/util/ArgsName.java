package ru.testapp.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ArgsName {
    private static final String PATH = "path";
    private static final String OUT = "out";
    private final Map<String, String> values = new HashMap<>();

    public String getOrDefault(String key, String defaultValue) {
        return values.getOrDefault(key, defaultValue);
    }

    public String get(String key) {
        if (!values.containsKey(key)) {
            throw new IllegalArgumentException("This key: '" + key + "' is missing");
        }
        return values.get(key
        );
    }

    private void parse(String[] args) {
        for (String it : args) {
            String[] tmpIt = it.substring(1).split("=", 2);
            values.put(tmpIt[0], tmpIt[1]);
        }
    }

    private static boolean argsValidation(String[] args) {
        boolean res = true;
        if (args.length == 0) {
            //throw new IllegalArgumentException("Arguments not passed to program");
            res = false;
        } else {
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
        return res;
    }

    private static ArgsName validationArgsAndIOparameters(String[] args) {
        ArgsName names = new ArgsName();
        if (argsValidation(args)) {
           names.parse(args);
            String sourcePath = names.get(PATH);
            String outputPath = names.get(OUT);
            if (!(Files.exists(Path.of(sourcePath)))) {
                throw new IllegalArgumentException("the selected folder " + sourcePath + " does not exist");
            }
            if (!(Files.exists(Path.of(outputPath)))) {
                throw new IllegalArgumentException("the selected folder " + outputPath + " does not exist");
            }
        }
        return names;
    }

    public static ArgsName of(String[] args) {
        return validationArgsAndIOparameters(args);
    }
}
