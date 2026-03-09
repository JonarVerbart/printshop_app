package com.example.util;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeFormatHandler {
    
    public String timestampToLocalDateTime(Timestamp timestamp) {
        if (timestamp != null) {
            String desiredPattern = "dd-MMM-yyyy HH:mm";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(desiredPattern);

            LocalDateTime localDateTime = timestamp.toLocalDateTime();
            return localDateTime.format(formatter);
        } else {
            return null;
        }
    }

    public Duration StringHHmmToDuration(String hhmm) {
        if (hhmm == null || !hhmm.matches("\\d{1,}:\\d{2}")) {
            throw new IllegalArgumentException("Invalid format. Expected H+:mm (e.g., 5:42 or 105:15)");
        }

        String[] parts = hhmm.split(":");
        long hours = Long.parseLong(parts[0]); // long to allow very large hours
        int minutes = Integer.parseInt(parts[1]);

        if (minutes < 0 || minutes > 59) {
            throw new IllegalArgumentException("Minutes must be between 0 and 59");
        }

        return Duration.ofHours(hours).plusMinutes(minutes);
    }

    public String durationHHmmToString(Duration duration) {
        boolean negative = duration.isNegative();
        if (negative) {
            duration = duration.negated();
        }

        long hours = duration.toHours();
        long minutes = duration.toMinutesPart(); // Java 9+
        String result = String.format("%d:%02d", hours, minutes);

        return negative ? "-" + result : result;
    }

    /**
 * Parses a duration string in "HH:mm" into total seconds.
 * - Supports optional leading sign ('-' or '+').
 * - Allows hours with 1+ digits (so values > 99 are OK).
 * - Validates minutes in 00..59.
 *
 * Examples:
 *   "00:00"  -> 0
 *   "02:15"  -> 8100
 *   "125:07" -> 450,420
 *   "-01:05" -> -3,900
 *
 * @param text duration string in HH:mm with optional sign
 * @return total seconds (may be negative)
 * @throws IllegalArgumentException if format is invalid
 */
public long parseHHmmToSeconds(String text) {
    if (text == null) {
        throw new IllegalArgumentException("duration must not be null");
    }
    String s = text.trim();
    if (s.isEmpty()) {
        throw new IllegalArgumentException("duration must not be empty");
    }

    boolean negative = false;
    char first = s.charAt(0);
    if (first == '-' || first == '+') {
        negative = (first == '-');
        s = s.substring(1);
        if (s.isEmpty()) {
            throw new IllegalArgumentException("sign provided but no digits: " + text);
        }
    }

    int colon = s.indexOf(':');
    if (colon <= 0 || colon != s.lastIndexOf(':')) {
        throw new IllegalArgumentException("Invalid duration format, expected HH:mm: " + text);
    }

    String hPart = s.substring(0, colon);       // 1+ digits
    String mPart = s.substring(colon + 1);      // must be exactly 2 digits

    if (mPart.length() != 2) {
        throw new IllegalArgumentException("Minutes must be two digits (MM): " + text);
    }

    long hours;
    int minutes;
    try {
        // hours may be arbitrarily large (>= 0)
        if (!hPart.chars().allMatch(Character::isDigit)) {
            throw new NumberFormatException("hours contain non-digits");
        }
        if (!mPart.chars().allMatch(Character::isDigit)) {
            throw new NumberFormatException("minutes contain non-digits");
        }
        hours = Long.parseLong(hPart);
        minutes = Integer.parseInt(mPart);
    } catch (NumberFormatException ex) {
        throw new IllegalArgumentException("Non-numeric HH or MM in duration: " + text, ex);
    }

    if (minutes < 0 || minutes > 59) {
        throw new IllegalArgumentException("Minutes must be in 00..59: " + text);
    }

    // Calculate safely with overflow checks
    long totalSeconds = Math.addExact(Math.multiplyExact(hours, 3600L), minutes * 60L);
    return negative ? -totalSeconds : totalSeconds;
}

}
