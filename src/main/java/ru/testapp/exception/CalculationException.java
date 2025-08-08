package ru.testapp.exception;

public class CalculationException extends ApplicationException {
    public CalculationException(String message, Throwable cause, int exitCode) {
        super(ErrorCode.CALCULATION_ERROR, message, exitCode);
    }

    public CalculationException(String message, int exitCode) {
        super(ErrorCode.CALCULATION_ERROR, message, exitCode);
    }
}
