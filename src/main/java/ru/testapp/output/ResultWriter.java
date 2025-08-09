package ru.testapp.output;

import ru.testapp.exception.DataLoadException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Запись результата в файл.
 */
public class ResultWriter {
    /**
     * Записывает строку в файл (перезапись).
     *
     * @param content содержимое
     * @param out путь к файлу
     */
    public void write(String content, Path out) {
        try {
            Files.writeString(out, content);
        } catch (IOException e) {
            throw new DataLoadException("Failed to write result: " + out, e, 6);
        }
    }
}
