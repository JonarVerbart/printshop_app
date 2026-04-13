package com.example.jsonrw;

import java.io.File;
import java.io.IOException;

import com.example.pojo.Order;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonReader {

    private static ObjectMapper objectMapper = new ObjectMapper();
    
    public JsonReader() {
        objectMapper.findAndRegisterModules();
    }

    public Order loadCartFromJsonFile(File jsonFile) {
        try {
            return objectMapper.readValue(jsonFile, Order.class);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

}
