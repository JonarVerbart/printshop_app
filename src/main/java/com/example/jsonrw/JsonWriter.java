package com.example.jsonrw;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonWriter {
    
    private static ObjectMapper objectMapper = new ObjectMapper();

    public JsonWriter() {

    }
        public void writeToJsonFile(Object object) {
        try {
            objectMapper.writeValue(new File("savedCart.json"), object);

        } catch (IOException e) {
            System.out.println("Something went wrong:");
            System.out.println(e.getClass().getSimpleName());
        }
    }
    
}

