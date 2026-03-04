package com.example.util;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.example.csvrw.CsvImporter;
import com.example.pojo.Order;

public class PickupTimeCalculator {
    
    Map<String, Map<String, String>> openingHours;

    public PickupTimeCalculator() {
        CsvImporter csvImporter = new CsvImporter(null);
        this.openingHours = csvImporter.getOpeningHoursFromCSV();
    }

    public ZonedDateTime calculatePickupTime(Order order) {

        TimeFormatHandler timeFormatHandler = new TimeFormatHandler();

        Instant utcNow = Instant.now();
        ZonedDateTime utcDateTimeNow = utcNow.atZone(ZoneOffset.UTC);
        DayOfWeek utcDayOfWeekNow = utcDateTimeNow.getDayOfWeek();

        //System.out.println(openingHours.get(utcDayOfWeekNow.toString().charAt(0) + utcDayOfWeekNow.toString().substring(1).toLowerCase()).get("openTill"));

        String[] todaysOpeningHourMinute;
        String[] todaysClosingHourMinute = openingHours.get(utcDayOfWeekNow.toString().charAt(0) + utcDayOfWeekNow.toString().substring(1).toLowerCase()).get("openTill").split(":");
        
        Integer todaysOpeningHour;
        Integer todaysOpeningMinute;
        Integer todaysClosingHour = Integer.parseInt(todaysClosingHourMinute[0]);
        Integer todaysClosingMinute = Integer.parseInt(todaysClosingHourMinute[1]);

        ZonedDateTime utcTodaysOpening;
        ZonedDateTime utcTodaysClosing = utcDateTimeNow.withHour(todaysClosingHour).withMinute(todaysClosingMinute);
        Duration nowTillClosingToday = Duration.between(utcDateTimeNow, utcTodaysClosing);

        Duration completionDurationLeft = order.getTotalCompletionTime();

        Duration openTillCloseToday;
        

        if (completionDurationLeft.minus(nowTillClosingToday).isNegative()) {
            ZonedDateTime pickupTime = utcDateTimeNow.plus(nowTillClosingToday.plus(completionDurationLeft));
            return pickupTime;
        } else {
            completionDurationLeft = completionDurationLeft.minus(nowTillClosingToday);
            int i = 1;
            while (true) {
                    // PROBABLY FIXED:There is a bug happening here, maybe when pcikup time is after closing time of order date. More likely: quantity not taken into account.
                    // BUG: Sunday is now probably interpreted as 24h of opening time instead of being closed the entire day. Can use different notation in CSV if easier.
                    System.out.println(utcDayOfWeekNow.plus(i).toString().charAt(0) + utcDayOfWeekNow.plus(i).toString().substring(1).toLowerCase());
                    todaysOpeningHourMinute = openingHours.get(utcDayOfWeekNow.plus(i).toString().charAt(0) + utcDayOfWeekNow.plus(i).toString().substring(1).toLowerCase()).get("openFrom").split(":");
                    todaysOpeningHour = Integer.parseInt(todaysOpeningHourMinute[0]);
                    todaysOpeningMinute = Integer.parseInt(todaysOpeningHourMinute[1]);
                    utcTodaysOpening = utcDateTimeNow.plusDays(i).withHour(todaysOpeningHour).withMinute(todaysOpeningMinute);

                    todaysClosingHourMinute = openingHours.get(utcDayOfWeekNow.plus(i).toString().charAt(0) + utcDayOfWeekNow.plus(i).toString().substring(1).toLowerCase()).get("openTill").split(":");
                    todaysClosingHour = Integer.parseInt(todaysClosingHourMinute[0]);
                    todaysClosingMinute = Integer.parseInt(todaysClosingHourMinute[1]);
                    utcTodaysClosing = utcDateTimeNow.plusDays(i).withHour(todaysClosingHour).withMinute(todaysClosingMinute);

                    openTillCloseToday = Duration.between(utcTodaysOpening, utcTodaysClosing);

                    if (completionDurationLeft.minus(openTillCloseToday).isNegative()) {
                        ZonedDateTime pickupTime = utcDateTimeNow.withHour(todaysOpeningHour).withMinute(todaysOpeningMinute).plusDays(i).plus(completionDurationLeft);
                        return pickupTime;
                    } else {
                        completionDurationLeft = completionDurationLeft.minus(openTillCloseToday);
                        i++;
                    }
                }

            }
                // If durationLeft - durationOpenNextDay = negative then return durationOpenNextDay - durationLeft as ZonedDateTime.
                // Else: durationLeft = durationLeft - durationOpenNextDay
        }

        //Duration timeLeftOnOrderDay = order.getOrderPlacedTimestamp();

        
        //totalCompletionTime - timeLeftOnOrderDay = 
}

