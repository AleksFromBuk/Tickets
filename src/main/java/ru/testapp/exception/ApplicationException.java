package ru.testapp.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {
    private final ErrorCode code;
    private final int exitCode;

    public ApplicationException(ErrorCode code, String message, int exitCode) {
        super(message);
        this.code = code;
        this.exitCode = exitCode;
    }

    public ApplicationException(ErrorCode code, String message, Throwable cause, int exitCode) {
        super(message, cause);
        this.code = code;
        this.exitCode = exitCode;
    }
}
