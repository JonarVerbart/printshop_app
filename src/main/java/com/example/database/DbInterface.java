package com.example.database;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
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
                INSERT INTO customers (email, password_hash, first_name, last_name, address, zip_code, city, phone_number)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

                try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, customer.getEmail());
                ps.setString(2, plainToHashed(plainPassword));
                ps.setString(3, customer.getFirstname());
                ps.setString(4, customer.getLastName());
                ps.setString(5, customer.getAddress());
                ps.setString(6, customer.getZipCode());
                ps.setString(7, customer.getCity());
                ps.setString(8, customer.getPhoneNumber());

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
                SELECT id, first_name, last_name, address, zip_code, city, phone_number
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
                        String address = rs.getString("address");
                        String zipCode = rs.getString("zip_code");
                        String city = rs.getString("city");
                        String phoneNumber = rs.getString("phone_number");
                        Customer customer = new Customer(id, email, firstName, lastName, address, zipCode, city, phoneNumber);
                        return customer;
                        }
                    }
                }
            } else {
                System.out.println("\nWrong password");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void insertItem(Item item) throws SQLException {
        String sqlQuery = """
                INSERT INTO items (product, size, finish, unit_price, completion_time_seconds)
                VALUES (?, ?, ?, ?, ?)
                """;

                try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, item.getProduct());
                ps.setString(2, item.getSize());
                ps.setString(3, item.getFinish());
                ps.setString(4, item.getUnitPrice());
                ps.setLong(5, item.getCompletionTime().getSeconds());

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
                SELECT id, product, size, finish, unit_price, completion_time_seconds
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
                        Long completionTimeLong = rs.getLong("completion_time_seconds");
                        Duration completionTime = Duration.ofSeconds(completionTimeLong);
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
        String sqlQuery1 = """
        ALTER TABLE order_item
        DROP FOREIGN KEY FK_OrderItem_Items, DROP INDEX FK_OrderItem_Items
        """;
        String sqlQuery2 = """
        DELETE from items WHERE id between 1 and 10000
        """;
        String sqlQuery3 = """
        ALTER TABLE items AUTO_INCREMENT = 1
        """;
        

        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuery1)) {
                ps.executeUpdate();
            }
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuery2)) {
                ps.executeUpdate();
            }
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuery3)) {
                ps.executeUpdate();
            }
        
    }

    public void addFkOrderItemItems() throws SQLException {
        String sqlQuery1 = """
        alter table order_item add constraint FK_OrderItem_Items foreign key(item_id) references items(id)
        """;
        try (Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuery1)) {
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

    public String retrieveUnitPrice(String productType, String size, String finish) {
        try {
        String sqlQuery = """
                SELECT unit_price FROM items
                WHERE product = ? and size = ? and finish = ?
                """;
            try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlQuery)) {

                ps.setString(1, productType);
                ps.setString(2, size);
                ps.setString(3, finish);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("unit_price");
                    }
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
                INSERT INTO orders (customer_id, order_placed, pickup_time, sub_total_cost, total_VAT, total_cost, order_status, notes)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

                try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, order.getCustomerId());
                ps.setTimestamp(2, order.getOrderPlacedTimestamp());
                ps.setTimestamp(3, order.getPickupTime());
                ps.setBigDecimal(4, order.getSubTotalCost());
                ps.setBigDecimal(5, order.getTotalVAT());
                ps.setBigDecimal(6, order.getTotalCost());
                ps.setInt(7, order.getStatus());
                ps.setString(8, order.getOrderNotes());

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

    public Order retrieveOrder(Integer orderId) {
        Order retrievedOrder = new Order();

        try {
        String sqlQuery = """
                SELECT *
                FROM orders
                WHERE id = ?
                """;

                try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlQuery)) {

                    ps.setInt(1, orderId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                        retrievedOrder.setId(rs.getInt("id"));
                        retrievedOrder.setCustomerId(rs.getInt("customer_id"));
                        retrievedOrder.setOrderPlacedTimestamp(rs.getTimestamp("order_placed"));
                        retrievedOrder.setPickupTime(rs.getTimestamp("pickup_time"));
                        retrievedOrder.setSubTotalCost(new BigDecimal(rs.getString("sub_total_cost")));
                        retrievedOrder.setTotalVAT(new BigDecimal(rs.getString("total_VAT")));
                        retrievedOrder.setTotalCost(new BigDecimal(rs.getString("total_cost")));
                        retrievedOrder.setStatus(rs.getInt("order_status"));
                        retrievedOrder.setOrderNotes(rs.getString("notes"));
                        }
                    }
                }    
                } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        try {
        String sqlQuery = """
                SELECT *
                FROM order_item
                WHERE order_id = ?
                """;

                try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlQuery)) {

                    ps.setInt(1, orderId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                        Item item = new Item();
                        
                        item.setId(rs.getInt("item_id"));
                        item.setQuantity(rs.getInt("quantity"));
                        item.setUnitPrice(rs.getString("unit_price_at_order_placed"));
                        item.setFullDisplayName(rs.getString("full_display_name"));

                        retrievedOrder.addItem(item);
                        }
                    }
                }    
                } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return retrievedOrder;

    }

    public List<TreeRowModel> retrieveAllCustomerOrderItems(String email) {
        try {
        String sqlQuery = """
                SELECT c.id customer_id, email, o.id order_id, order_placed, pickup_time, sub_total_cost, total_VAT, total_cost, order_status, item_id, quantity, unit_price_at_order_placed, full_display_name 
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
                        //Integer customerId = rs.getInt("customer_id");
                        Integer orderId = rs.getInt("order_id");
                        Timestamp orderPlaced = rs.getTimestamp("order_placed");
                        Timestamp pickupTime = rs.getTimestamp("pickup_time");
                        BigDecimal subtotal = rs.getBigDecimal("sub_total_cost");
                        BigDecimal totalVat = rs.getBigDecimal("total_VAT");
                        BigDecimal totalCost = rs.getBigDecimal("total_cost");
                        Integer orderStatus = rs.getInt("order_status");
                        //Integer itemId = rs.getInt("item_id");
                        Integer quantity = rs.getInt("quantity");
                        BigDecimal unitPriceAtOrderPlaced = rs.getBigDecimal("unit_price_at_order_placed");
                        String fullDisplayName = rs.getString("full_display_name");
 
                        treeRowModel = new TreeRowModel();
                        treeRowModel.orderIdProperty().set(orderId.toString());
                        treeRowModel.fullDisplayNameProperty().set(fullDisplayName);
                        treeRowModel.timestampPlacedProperty().set(orderPlaced);
                        treeRowModel.statusProperty().set(orderStatus.toString());
                        treeRowModel.pickupTimeProperty().set(pickupTime);
                        treeRowModel.unitPriceProperty().set(unitPriceAtOrderPlaced);
                        treeRowModel.quantityProperty().set(quantity);
                        treeRowModel.subtotalProperty().set(subtotal);
                        treeRowModel.vatProperty().set(totalVat);
                        treeRowModel.totalCostProperty().set(totalCost);

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

    public List<TreeRowModel> retrieveAllCustomerOrders(String email) {
try {
        String sqlQuery = """
                SELECT c.id customer_id, email, o.id order_id, order_placed, pickup_time, sub_total_cost, total_VAT, total_cost, order_status 
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
                        Integer orderStatus = rs.getInt("order_status");
 
                        treeRowModel = new TreeRowModel();
                        treeRowModel.orderIdProperty().set(orderId.toString());
                        treeRowModel.timestampPlacedProperty().set(orderPlaced);
                        treeRowModel.statusProperty().set(orderStatus.toString());
                        treeRowModel.pickupTimeProperty().set(pickupTime);
                        treeRowModel.subtotalProperty().set(subtotal);
                        treeRowModel.vatProperty().set(totalVat);
                        treeRowModel.totalCostProperty().set(totalCost);

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

    public String retrieveOrderNotes(Integer orderId) {
        try {
        String sqlQuery = """
                SELECT notes FROM orders order_item
                WHERE id = ?
                """;

                try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlQuery)) {
                    ps.setInt(1, orderId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                        //Integer customerId = rs.getInt("customer_id");
                        String orderNotes = rs.getString("notes");
                        return orderNotes;
                        }
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
