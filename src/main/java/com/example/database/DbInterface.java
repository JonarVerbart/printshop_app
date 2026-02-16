package com.example.database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.mindrot.jbcrypt.BCrypt;

import com.example.pojo.Customer;

public class DbInterface {

    private final DataSource dataSource;

    public DbInterface(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public String plainToHashed(String plainPassword) {
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        return hashedPassword;
    }

    public boolean checkPassword(String plainPassword, String hashedPassword) {
        boolean passwordCorrect = BCrypt.checkpw(plainPassword, hashedPassword);
        return passwordCorrect;
    }

    public void insertCustomer(Customer customer, String plainPassword) throws SQLException {
        String sqlQuery = """
                INSERT INTO customers (email, password_hash, first_name, last_name)
                VALUES (?, ?, ?, ?)
                """;

                try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, customer.getEmail());
                ps.setString(2, plainToHashed(plainPassword));
                ps.setString(3, customer.getFirstname());
                ps.setString(4, customer.getLastName());

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        customer.setId(rs.getInt(1));
                    }
                }
            }
    }

    public String retrieveHashedPassword(String email) throws SQLException {
        String sqlQuery = """
        SELECT id, email, password_hash, first_name, last_name
        FROM customers
        WHERE email = ?
        """;

        try (Connection conn = dataSource.getConnection();
         PreparedStatement ps = conn.prepareStatement(sqlQuery)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password_hash");
                    return hashedPassword;
                }
            }
        }
        return null;
    }

    public void updateCustomer(String email, String newEmail, String newFirstName, String newLastName) throws SQLException {
        String sqlQuery = """
        UPDATE customers SET email = ?, first_name = ?, last_name = ?
        WHERE email = ?
        """;

        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuery)) {
                ps.setString(1, newEmail);
                ps.setString(2, newFirstName);
                ps.setString(3, newLastName);
                ps.setString(4, email);

                ps.executeUpdate();
            }
    }

    public Customer retrieveCustomer(String plainPassword, String email) {
        try {
            if (checkPassword(plainPassword, retrieveHashedPassword(email))) {
                String sqlQuery = """
                SELECT id, first_name, last_name
                FROM customers
                WHERE email = ?
                """;

                try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlQuery)) {

                    ps.setString(1, email);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                        String firstName = rs.getString("first_name");
                        String lastName = rs.getString("last_name");
                        Integer id = rs.getInt("id");
                        Customer customer = new Customer(id, email, firstName, lastName);
                        return customer;
                        }
                    }
                }
            } else {
                System.out.println("Wrong password");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    
}
