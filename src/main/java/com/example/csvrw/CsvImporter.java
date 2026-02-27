package com.example.csvrw;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.constants.Constants;
import com.example.database.DbInterface;
import com.example.pojo.Item;
import com.example.util.TimeFormatHandler;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class CsvImporter {

    protected DbInterface dbInterface;

    public CsvImporter(DbInterface dbInterface) {
        this.dbInterface = dbInterface;
    }
    
    public void inventoryCsvToDb() {

        TimeFormatHandler timeFormatHandler = new TimeFormatHandler();

        List<List<String>> inventory = new ArrayList<List<String>>();
        try(CSVReader csvReader = new CSVReader(new FileReader("src/main/resources/inventory_expanded_nobom_redacted.csv"));) {
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                inventory.add(Arrays.asList(values));
            }
        } catch (CsvValidationException | IOException e) {
            System.out.println(e.getMessage());
        } 
        //inventory.forEach(inner -> System.out.println(inner));

        //List<Item> items = new ArrayList<>();
        inventory.forEach(innerList -> {
            String line = innerList.get(0);
            line = line.replace("[", "").replace("]", "");
            String[] parts = line.split(";");

            Long completionTimeLong = timeFormatHandler.parseHHmmToSeconds(parts[5]);
            Duration completionTime = Duration.ofSeconds(completionTimeLong);

            Item item = new Item(parts[1], parts[2], parts[3], parts[4], completionTime);
            try {
                dbInterface.insertItem(item);
            } catch (SQLException e) {
                e.getMessage();
            }
        });
        try {
            dbInterface.addFkOrderItemItems();
        } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
}

    public Map<String, Map<String, String>> getOpeningHoursFromCSV() {
        List<List<String>> openingHours = new ArrayList<List<String>>();

        try(CSVReader csvReader = new CSVReader(new FileReader("src/main/resources/PhotoShop_OpeningHours.csv"));) {
            csvReader.readNext();
            String[] values = null;
            while ((values = csvReader.readNext()) != null) {
                openingHours.add(Arrays.asList(values));
            }
            
        } catch (CsvValidationException | IOException e) {
            System.out.println(e.getMessage());
        }
        
        Map<String, Map<String, String>> outerMap = new HashMap<>();

        openingHours.forEach(innerList -> {
            String line = innerList.get(0);
            line = line.replace("[", "").replace("]", "");
            String[] parts = line.split(";");

            String dayOfTheWeek = parts[1];
            String openFrom = parts[2];
            String openTill = parts[3];

            Map<String, String> innerMap = new HashMap<>();
            innerMap.put("openFrom", openFrom);
            innerMap.put("openTill", openTill);
            outerMap.put(dayOfTheWeek, innerMap);
        });
        return outerMap;
    }

}
