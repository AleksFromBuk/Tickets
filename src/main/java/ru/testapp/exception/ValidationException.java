package ru.testapp.exception;

/**
 * Исключение в случае неверных входных параметров.
 */
public class ValidationException extends ApplicationException {
    public ValidationException(String message, int exitCode) {
        super(ErrorCode.INVALID_ARGUMENTS, message, exitCode);
    }
}
