package com.example.database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.mindrot.jbcrypt.BCrypt;

import com.example.pojo.Customer;
import com.example.pojo.Item;

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

    public void insertItem(Item item) throws SQLException {
        String sqlQuery = """
                INSERT INTO items (product, size, finish, unit_price, completion_time)
                VALUES (?, ?, ?, ?, ?)
                """;

                try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, item.getProduct());
                ps.setString(2, item.getSize());
                ps.setString(3, item.getFinish());
                ps.setString(4, item.getUnitPrice());
                ps.setString(5, item.getCompletionTime());

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        item.setId(rs.getInt(1));
                    }
                }
            }
    }

    public Item retrieveItem(String product, String size, String finish) {
        try {
        String sqlQuery = """
                SELECT id, product, size, finish, unit_price, completion_time
                FROM items
                WHERE product = ? and size = ? and finish = ?
                """;

                try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlQuery)) {

                    ps.setString(1, product);
                    ps.setString(2, size);
                    ps.setString(3, finish);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                        Integer id = rs.getInt("id");
                        String unitPrice = rs.getString("unit_price");
                        String completionTime = rs.getString("completion_time");
                            Item item = new Item(id, product, size, finish, unitPrice, completionTime);
                            return item;
                        }
                    }
                }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            return null;
    }

    public void clearItemTable() throws SQLException {
        String sqlQuery = """
        DELETE from items WHERE id between 1 and 10000
        """;
        String sqlQuery2 = """
        ALTER TABLE items AUTO_INCREMENT = 1
        """;

        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuery)) {
                ps.executeUpdate();
            }
        
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuery2)) {
                ps.executeUpdate();
            }
    }

    public ArrayList<String> retrieveDistinctProducts() {
        try {
        String sqlQuery = """
                SELECT DISTINCT product
                FROM items
                """;
                try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlQuery)) {

                    try (ResultSet rs = ps.executeQuery()) {
                        ArrayList<String> distinctProducts = new ArrayList<String>();
                        while (rs.next()) {
                        distinctProducts.add(rs.getString("product"));
                        }
                        return distinctProducts;
                    }
                }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            return null;
    }

    public ArrayList<String> retrieveDistinctSizes(String productType) {
        try {
        String sqlQuery = """
                SELECT DISTINCT size
                FROM items
                WHERE product = ?
                """;

                try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlQuery)) {
                    ps.setString(1, productType);
                    try (ResultSet rs = ps.executeQuery()) {
                        ArrayList<String> distinctSizes = new ArrayList<String>();
                        while (rs.next()) {
                        distinctSizes.add(rs.getString("size"));
                        }
                        return distinctSizes;
                    }
                }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            return null;
    }

    public ArrayList<String> retrieveDistinctFinishes(String productType) {
        try {
        String sqlQuery = """
                SELECT DISTINCT finish
                FROM items
                WHERE product = ?
                """;

                try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlQuery)) {
                    ps.setString(1, productType);
                    try (ResultSet rs = ps.executeQuery()) {
                        ArrayList<String> distinctFinishes = new ArrayList<String>();
                        while (rs.next()) {
                        distinctFinishes.add(rs.getString("finish"));
                        }
                        return distinctFinishes;
                    }
                }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            return null;
    }
    
}
