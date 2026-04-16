package com.example;

import com.example.javafx.Javafx;

import javafx.application.Application;

/**
 * Compile using this command: mvn clean compile assembly:single
 * Run in command prompt: java -jar filename.jar
 * To fix App.java not on classpath after pom.xml chages: Java: Clean Java Language Server Workspace in command palette
 */
public class App 
{
    public static void main( String[] args )
    {
        Application.launch(Javafx.class, args);
    }
}
