package com.example.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimestampFormatter {
    
    public String timestampToLocalDateTime(Timestamp timestamp) {
        String desiredPattern = "dd-MMM-yyyy hh:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(desiredPattern);

        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        return localDateTime.format(formatter);
    }

}
