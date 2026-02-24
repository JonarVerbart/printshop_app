package com.example.database;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.mindrot.jbcrypt.BCrypt;

import com.example.pojo.Customer;
import com.example.pojo.Item;
import com.example.pojo.Order;
import com.example.pojo.TreeRowModel;

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

    public void clearOrderItemTable() throws SQLException {
        String sqlQuery = """
        DELETE from order_item WHERE id between 1 and 10000
        """;
        String sqlQuery2 = """
        ALTER TABLE order_item AUTO_INCREMENT = 1
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

    public void clearOrdersTable() throws SQLException {
        String sqlQuery = """
        DELETE from orders WHERE id between 1 and 10000
        """;
        String sqlQuery2 = """
        ALTER TABLE orders AUTO_INCREMENT = 1
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

    public void clearItemsTable() throws SQLException {
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

    public void insertOrder(Order order) {
        try {
        String sqlQuery = """
                INSERT INTO orders (customer_id, order_placed, sub_total_cost, total_VAT, total_cost, order_closed)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

                try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, order.getCustomerId());
                ps.setTimestamp(2, order.getOrderPlacedTimestamp());
                ps.setBigDecimal(3, order.getSubTotalCost());
                ps.setBigDecimal(4, order.getTotalVAT());
                ps.setBigDecimal(5, order.getTotalCost());
                ps.setBoolean(6, order.getOrderClosed());

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        
                        order.setId(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
    }

    public List<TreeRowModel> retrieveAllCustomerOrderItems(String email) {
        try {
        String sqlQuery = """
                SELECT c.id customer_id, email, o.id order_id, order_placed, pickup_time, sub_total_cost, total_VAT, total_cost, order_closed, item_id, quantity, unit_price_at_order_placed, full_display_name 
                FROM customers c 
                JOIN orders o on c.id = o.customer_id 
                JOIN order_item oi on oi.order_id = o.id 
                WHERE email = ?
                """;

                try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlQuery)) {

                    List<TreeRowModel> treeRowList = new ArrayList<>();
                    TreeRowModel treeRowModel;

                    ps.setString(1, email);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                        Integer customerId = rs.getInt("customer_id");
                        Integer orderId = rs.getInt("order_id");
                        Timestamp orderPlaced = rs.getTimestamp("order_placed");
                        Timestamp pickupTime = rs.getTimestamp("pickup_time");
                        BigDecimal subtotal = rs.getBigDecimal("sub_total_cost");
                        BigDecimal totalVat = rs.getBigDecimal("total_VAT");
                        BigDecimal totalCost = rs.getBigDecimal("total_cost");
                        Boolean orderClosed = rs.getBoolean("order_closed");
                        Integer itemId = rs.getInt("item_id");
                        Integer quantity = rs.getInt("quantity");
                        BigDecimal unitPriceAtOrderPlaced = rs.getBigDecimal("unit_price_at_order_placed");
                        String fullDisplayName = rs.getString("full_display_name");
 
                        treeRowModel = new TreeRowModel();
                        treeRowModel.orderProperty().set(fullDisplayName);
                        treeRowModel.dateProperty().set(orderPlaced);
                        treeRowModel.statusProperty().set(orderClosed.toString());
                        treeRowModel.pickupTimeProperty().set(pickupTime);
                        treeRowModel.unitPriceProperty().set(unitPriceAtOrderPlaced);
                        treeRowModel.quantityProperty().set(quantity);
                        treeRowModel.subtotalProperty().set(subtotal);
                        treeRowModel.vatProperty().set(totalVat);
                        treeRowModel.totalCostProperty().set(totalCost);

                        treeRowList.add(treeRowModel);
                        
                        System.out.println("\n--------Table row----------\n");
                        System.out.println(customerId);
                        System.out.println(orderId);
                        System.out.println(orderPlaced);
                        System.out.println(pickupTime);
                        System.out.println(subtotal);
                        System.out.println(totalVat);
                        System.out.println(totalCost);
                        System.out.println(orderClosed);
                        System.out.println(itemId);
                        System.out.println(quantity);
                        System.out.println(unitPriceAtOrderPlaced);
                        System.out.println(fullDisplayName + "\n");

                        
                        
                        // Item item = new Item(id, product, size, finish, unitPrice, completionTime);
                        // return item;
                        }
                        return treeRowList;
                    }
                }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
        return null;
    }

    public List<TreeRowModel> retrieveAllCustomerOrders(String email) {
try {
        String sqlQuery = """
                SELECT c.id customer_id, email, o.id order_id, order_placed, pickup_time, sub_total_cost, total_VAT, total_cost, order_closed 
                FROM customers c 
                JOIN orders o on c.id = o.customer_id
                WHERE email = ?
                GROUP BY order_id
                """;

                try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlQuery)) {

                    List<TreeRowModel> treeRowList = new ArrayList<>();
                    TreeRowModel treeRowModel;

                    ps.setString(1, email);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                        Integer orderId = rs.getInt("order_id");
                        Timestamp orderPlaced = rs.getTimestamp("order_placed");
                        Timestamp pickupTime = rs.getTimestamp("pickup_time");
                        BigDecimal subtotal = rs.getBigDecimal("sub_total_cost");
                        BigDecimal totalVat = rs.getBigDecimal("total_VAT");
                        BigDecimal totalCost = rs.getBigDecimal("total_cost");
                        Boolean orderClosed = rs.getBoolean("order_closed");
 
                        treeRowModel = new TreeRowModel();
                        treeRowModel.orderProperty().set(orderId.toString());
                        treeRowModel.dateProperty().set(orderPlaced);
                        treeRowModel.statusProperty().set(orderClosed.toString());
                        treeRowModel.pickupTimeProperty().set(pickupTime);
                        treeRowModel.subtotalProperty().set(subtotal);
                        treeRowModel.vatProperty().set(totalVat);
                        treeRowModel.totalCostProperty().set(totalCost);

                        System.out.println("Is the bus still runnin?");
                        treeRowList.add(treeRowModel);
                        }
                        return treeRowList;
                    }
                }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
        return null;
    }

    public void insertOrderItem(Integer orderId, Integer itemId, Integer quantity, String unitPriceAtOrderTime, String fullDisplayName) {
        try {
        String sqlQuery = """
                INSERT INTO order_item (order_id, item_id, quantity, unit_price_at_order_placed, full_display_name)
                VALUES (?, ?, ?, ?, ?)
                """;

                try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, orderId);
                ps.setInt(2, itemId);
                ps.setInt(3, quantity);
                ps.setString(4, unitPriceAtOrderTime);
                ps.setString(5, fullDisplayName);

                ps.executeUpdate();
            }
        } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
    }

}
