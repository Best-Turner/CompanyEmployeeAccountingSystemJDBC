package org.example.exception;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class DepartmentNotFoundException extends RuntimeException{

    private final ExceptionBody body;

    public DepartmentNotFoundException(String text) {
        super(text);
        body = new ExceptionBody(404, text, Timestamp.valueOf(LocalDateTime.now()));
    }
}
