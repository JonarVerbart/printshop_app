package com.example.jsonrw;

import java.io.File;
import java.io.IOException;

import com.example.pojo.Order;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonReader {

    private static ObjectMapper objectMapper = new ObjectMapper();
    
    public Order loadCartFromJsonFile() {
        try {
            return objectMapper.readValue(new File("savedCart.json"), Order.class);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

}
