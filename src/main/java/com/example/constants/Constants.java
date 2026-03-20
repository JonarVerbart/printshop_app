package com.example.constants;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Constants {
    
    public static final boolean CLEAR_ORDERS = false;  // Clear orders and order_item tables in database on application close
    public static final boolean CLEAR_ITEMS = false;   // Clear items table in database on application close
    public static final boolean LOAD_ITEMS = false; // Load items on opplication load
    
    public static final String COMPANY_NAME = "PhotoShop PhotoShop";

    public static final Path COMPANY_LOGO_PATH = Paths.get("src/main/resources/PhotoShop.png");
    public static final Path INVOICE_SAVE_PATH = Path.of(System.getProperty("user.dir") + "/invoice.pdf");
}
