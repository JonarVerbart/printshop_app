package com.example.javafx;

import com.example.database.DbInterface;
import com.example.pojo.Customer;

public abstract class BaseController {

    protected DbInterface dbInterface;
    protected Customer loggedCustomer;

    public void setDbInterface(DbInterface dbInterface) {
        this.dbInterface = dbInterface;
    }

    public void setLoggedCustomer(Customer loggedCustomer) {
        this.loggedCustomer = loggedCustomer;
    }
}
