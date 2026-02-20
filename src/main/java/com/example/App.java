package com.example;

import com.example.javafx.Javafx;
import com.example.jsonrw.JsonWriter;

import javafx.application.Application;

/**
 * Hello world!
 *
 * Compile using this command: mvn clean compile assembly:single
 * Run in command prompt: java -jar filename.jar
 */
public class App 
{
    static JsonWriter jsonWriter = new JsonWriter();
    public static void main( String[] args )
    {
        Application.launch(Javafx.class, args);
    }
}
