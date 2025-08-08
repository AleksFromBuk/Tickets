package ru.testapp.exception;

import ru.testapp.Application;

/**
 * Исключение при загрузке/парсинге данных
 */
public class DataLoadException extends ApplicationException {
    public DataLoadException(String message, Throwable cause, int exitCode) {
        super(ErrorCode.FILE_READ_ERROR, message, cause, exitCode);
    }

    public DataLoadException(String message, int exitCode) {
        super(ErrorCode.FILE_READ_ERROR, message, exitCode);
    }
}
