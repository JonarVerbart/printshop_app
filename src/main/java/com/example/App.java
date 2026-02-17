package com.example;

import com.example.javafx.Javafx;
import com.example.jsonrw.JsonWrite;

import javafx.application.Application;

/**
 * Hello world!
 *
 * Compile using this command: mvn clean compile assembly:single
 * Run in command prompt: java -jar filename.jar
 */
public class App 
{
    static JsonWrite jsonWriter = new JsonWrite();
    public static void main( String[] args )
    {
        //jsonWriter.writeToJsonFile(loginRepository.getMap());

        Application.launch(Javafx.class, args);
    }
}
