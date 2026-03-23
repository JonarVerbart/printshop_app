package com.example.util;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;

import com.example.csvrw.CsvImporter;
import com.example.pojo.Order;

public class PickupTimeCalculator {
    
    Map<String, Map<String, String>> openingHours;

    public PickupTimeCalculator() {
        CsvImporter csvImporter = new CsvImporter(null);
        this.openingHours = csvImporter.getOpeningHoursFromCSV();
    }


    public Duration calculateTotalCompletionTime(Order order) {
        Duration totalCompletionTime = Duration.ofSeconds(0);
        for (var item : order.getItems()) {
            totalCompletionTime = totalCompletionTime.plus(item.getCompletionTime().multipliedBy(item.getQuantity()));
        };
        return totalCompletionTime;
    }

    public Timestamp calculatePickupTime(Order order) {
        Instant utcNow = Instant.now();
        ZonedDateTime utcDateTimeNow = utcNow.atZone(ZoneOffset.UTC);
        DayOfWeek utcDayOfWeekNow = utcDateTimeNow.getDayOfWeek();

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
            ZonedDateTime pickupTime = utcDateTimeNow.plus(completionDurationLeft);
            //System.out.println("Now till closing today: " + nowTillClosingToday);
            System.out.println("\nCan be picked up same day at: " + Timestamp.from(pickupTime.toInstant()));
            return Timestamp.from(pickupTime.toInstant());  // Timestamp converts from UTC to system time. MySQL converts back to UTC.
        } else {
            completionDurationLeft = completionDurationLeft.minus(nowTillClosingToday);
            int i = 1;
            while (true) {
                    System.out.println("\n" + utcDayOfWeekNow.plus(i).toString().charAt(0) + utcDayOfWeekNow.plus(i).toString().substring(1).toLowerCase());
                    todaysOpeningHourMinute = openingHours.get(utcDayOfWeekNow.plus(i).toString().charAt(0) + utcDayOfWeekNow.plus(i).toString().substring(1).toLowerCase()).get("openFrom").split(":");
                    todaysOpeningHour = Integer.parseInt(todaysOpeningHourMinute[0]);
                    todaysOpeningMinute = Integer.parseInt(todaysOpeningHourMinute[1]);
                    utcTodaysOpening = utcDateTimeNow.plusDays(i).withHour(todaysOpeningHour).withMinute(todaysOpeningMinute);

                    todaysClosingHourMinute = openingHours.get(utcDayOfWeekNow.plus(i).toString().charAt(0) + utcDayOfWeekNow.plus(i).toString().substring(1).toLowerCase()).get("openTill").split(":");
                    todaysClosingHour = Integer.parseInt(todaysClosingHourMinute[0]);
                    todaysClosingMinute = Integer.parseInt(todaysClosingHourMinute[1]);
                    utcTodaysClosing = utcDateTimeNow.plusDays(i).withHour(todaysClosingHour).withMinute(todaysClosingMinute);

                    openTillCloseToday = Duration.between(utcTodaysOpening, utcTodaysClosing);
                    System.out.println("Duration open today: " + openTillCloseToday);

                    if (completionDurationLeft.minus(openTillCloseToday).isNegative()) {
                        ZonedDateTime pickupTime = utcDateTimeNow.withHour(todaysOpeningHour).withMinute(todaysOpeningMinute).plusDays(i).plus(completionDurationLeft);
                        System.out.println("Completion time left (Add to opening time of pickup day): " + completionDurationLeft);
                        System.out.println("Pickup time: " + Timestamp.from(pickupTime.toInstant()));
                        return Timestamp.from(pickupTime.toInstant());
                    } else {
                        completionDurationLeft = completionDurationLeft.minus(openTillCloseToday);
                        System.out.println("Completion time left: " + completionDurationLeft);
                        i++;
                    }
                }
            }
        }
 
}

