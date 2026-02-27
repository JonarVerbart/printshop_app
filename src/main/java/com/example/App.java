package com.example;

import com.example.javafx.Javafx;
import com.example.util.PickupTimeCalculator;

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

        //PickupTimeCalculator pickupTimeCalculator = new PickupTimeCalculator();
        //pickupTimeCalculator.calculatePickupTime(null);

        Application.launch(Javafx.class, args);
    }
}
