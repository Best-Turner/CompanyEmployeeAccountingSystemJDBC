package org.example.exception;

public class EmployeeNotFoundException extends NotFoundException {

    public EmployeeNotFoundException(String text) {
        super(text);
    }
}
