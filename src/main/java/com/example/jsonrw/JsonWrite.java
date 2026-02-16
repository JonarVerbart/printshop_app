package com.example.jsonrw;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonWrite {
    
    private static ObjectMapper objectMapper = new ObjectMapper();

    public JsonWrite() {

    }

    public void writeToJsonFile(Map<String, String> map) {
        try {
            objectMapper.writeValue(new File("repotojson.json"), map);

        } catch (IOException e) {
            System.out.println("Something went wrong:");
            System.out.println(e.getClass().getSimpleName());
        }
    }

}

