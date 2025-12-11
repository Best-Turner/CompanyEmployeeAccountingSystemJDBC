package org.example.exception;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public abstract class NotFoundException extends RuntimeException {
    private final ExceptionBody body;

    public NotFoundException(String text) {
        super(text);
        this.body = new ExceptionBody(404, text, Timestamp.valueOf(LocalDateTime.now()));
    }

    public ExceptionBody getBody() {
        return body;
    }
}
