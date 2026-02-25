package com.example.util;

import java.time.DayOfWeek;
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

    public void calculatePickupTime(Order order) {

        Instant nowUtc = Instant.now();
        ZonedDateTime utcDateTime = nowUtc.atZone(ZoneOffset.UTC);
        DayOfWeek dayOfWeek = utcDateTime.getDayOfWeek();

        System.out.println(openingHours.get(dayOfWeek.toString().charAt(0) + dayOfWeek.toString().substring(1).toLowerCase()).get("openTill"));
    }
}
