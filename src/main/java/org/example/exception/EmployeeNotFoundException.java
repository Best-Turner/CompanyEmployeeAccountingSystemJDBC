package org.example.exception;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class EmployeeNotFoundException extends NotFoundException {

    public EmployeeNotFoundException(String text) {
        super(text);
    }
}
