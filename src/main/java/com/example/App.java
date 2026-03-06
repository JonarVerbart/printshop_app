package com.example;

import com.example.javafx.Javafx;

import javafx.application.Application;

/**
 * Hello world!
 *
 * Compile using this command: mvn clean compile assembly:single
 * Run in command prompt: java -jar filename.jar
 */
public class App 
{
    public static void main( String[] args )
    {
        Application.launch(Javafx.class, args);
    }
}
