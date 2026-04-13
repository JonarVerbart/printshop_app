package com.example.jsonrw;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonWriter {
    
    private static ObjectMapper objectMapper = new ObjectMapper();

    public JsonWriter() {
        objectMapper.findAndRegisterModules();
    }
    
    public void writeToJsonFile(Object object, File jsonFile) {
        try {
            objectMapper.writeValue(jsonFile, object);

        } catch (IOException e) {
            System.out.println("\nSomething went wrong:");
            System.out.println(e.getClass().getSimpleName());
        }
    }
    
}

