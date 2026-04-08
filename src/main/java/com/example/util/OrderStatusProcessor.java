package com.example.util;

public class OrderStatusProcessor {
    
    public String getStatusDisplayName(String statusIntString) {

        int statusInt = Integer.parseInt(statusIntString);

        switch (statusInt) {
            case 0:
                return "Order recieved, awaiting payment";
            case 1:
                return "Payment recieved";
            case 2:
                return "Making your items";
            case 3:
                return "Preparing to ship";
            case 4:
                return "Order shipped";
            case 5:
                return "Recieved by local carrier";
            case 6:
                return "Order completed";
            case 99:
                return "Cancelled";
            default:
                return "Status unknown, contact support";
        }
    }

}
