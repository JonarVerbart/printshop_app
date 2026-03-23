package com.example.javafx;

import com.example.database.DbInterface;
import com.example.pojo.Customer;

import javafx.stage.Stage;

public abstract class BaseController {

    protected Stage stage;
    protected DbInterface dbInterface;
    protected Customer loggedCustomer;

    public abstract void setStage(Stage stage, String previousFxml);

    public void setDbInterface(DbInterface dbInterface) {
        this.dbInterface = dbInterface;
    }

    public void setLoggedCustomer(Customer loggedCustomer) {
        this.loggedCustomer = loggedCustomer;
    }

    public abstract void initializeFromDb();
}
