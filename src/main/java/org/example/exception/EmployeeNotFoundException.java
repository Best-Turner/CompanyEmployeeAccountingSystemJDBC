package org.example.exception;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class EmployeeNotFoundException extends RuntimeException {

    private final ExceptionBody body;

    public EmployeeNotFoundException(String text) {
        body = new ExceptionBody(404, text, Timestamp.valueOf(LocalDateTime.now()));
    }

    public ExceptionBody getBody() {
        return body;
    }
}
