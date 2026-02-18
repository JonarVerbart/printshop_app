package com.example.csvrw;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.database.DbInterface;
import com.example.pojo.Item;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class CsvImporter {

    protected DbInterface dbInterface;

    public CsvImporter(DbInterface dbInterface) {
        this.dbInterface = dbInterface;
    }
    
    public void inventoryCsvToDb() {

        List<List<String>> inventory = new ArrayList<List<String>>();
        try(CSVReader csvReader = new CSVReader(new FileReader("src/main/resources/inventory_expanded_nobom.csv"));) {
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

            Item item = new Item(parts[1], parts[2], parts[3], parts[4], parts[5]);
            try {
                dbInterface.insertItem(item);
            } catch (SQLException e) {
                e.getMessage();
            }

        });

    }

}
