package com.example.pojo;

public class Customer {
    
    private Integer id;
    private String email;
    private String firstname;
    private String lastName;


    public Customer(String email, String firstName, String lastName) {
        this.email = email;
        this.firstname = firstName;
        this.lastName = lastName;
    }

    public Customer(Integer id, String email, String firstName, String lastName) {
        this.id = id;
        this.email = email;
        this.firstname = firstName;
        this.lastName = lastName;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


}
