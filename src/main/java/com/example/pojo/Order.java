package com.example.pojo;


/**
 * MySQL uses datetime type and looks like this: '2023-01-31 23:59:59.999999'
 * Figure out how to interface that between SQL and Java
 * 
 * You can just do this (importing java.sql.*):
 *  Timestamp ts = rs.getTimestamp(1);
        System.out.println("Timestamp: " + ts);
    
            will show: Timestamp: 2026-02-18 14:35:00.0
 */

public class Order {
    

}
