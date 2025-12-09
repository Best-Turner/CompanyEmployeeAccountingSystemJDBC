package org.example.exception;

import java.sql.Timestamp;

public class ExceptionBody {

    private final int code;
    private final String message;
    private final Timestamp timestamp;

    public ExceptionBody(int code, String message, Timestamp timestamp) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ExceptionBody{" +
               "code=" + code +
               ", message='" + message + '\'' +
               ", timestamp=" + timestamp +
               '}';
    }
}
