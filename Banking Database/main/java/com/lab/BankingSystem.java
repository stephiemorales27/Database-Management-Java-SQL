package com.lab;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BankingSystem {

    public static void main(String[] args) {
        Connection conn = null;
        try {
            // establish the connection to MySQL
            conn = getDatabaseConnection();
            // check connection is not null before using it
            if (conn != null) {
                // Start a transaction
                conn.setAutoCommit(false);
                BankingSystem.createDatabase(conn);
                BankingSystem.createTables(conn);

                Customer customer1 = new Customer(0, "John Doe", "123 Main St");
                BankingSystem.createCustomerAccount(conn, customer1, 500.00);
                Customer customer2 = new Customer(0, "Jane Smith", "456 Oak St");
                BankingSystem.createCustomerAccount(conn, customer2, 1000.00);

                customer1.setAddress("456 New Address");
                BankingSystem.updateCustomerDetails(conn, customer1);

                BankingSystem.viewAllCustomers(conn);
                BankingSystem.deleteCustomerAccount(conn, 1);
            }
        } catch (SQLException e) {
            System.err.println("Main SQLException :" + e.getMessage());
        } finally {
            // close the connection if it was successfully opened
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                    System.out.println("Auto-commit restored and connection closed.");
                } catch (SQLException e) {
                    System.err.println("Error closing the database connection: " + e.getMessage());
                }
            }
        }
    }

    // Task 1: Create a new customer and their account
    public static void createCustomerAccount(Connection conn, Customer customer, double initialBalance) {
        String customerInsertSQL = "INSERT INTO customers (name, address) VALUES (?, ?)";
        String accountInsertSQL = "INSERT INTO accounts (customer_id, balance) VALUES (?, ?)";

        try {
            // Insert new customer into customers table
            try (PreparedStatement customerStmt = conn.prepareStatement(customerInsertSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                
                customerStmt.setString(1, customer.getName());    // name
                customerStmt.setString(2, customer.getAddress()); // address

                customerStmt.executeUpdate();

                // Get generated customer ID
                ResultSet generatedKeys = customerStmt.getGeneratedKeys();
                int customerId = 0;
                if (generatedKeys.next()) {
                    customerId = generatedKeys.getInt(1);
                    customer.setId(customerId);  // Set the generated ID to the customer object
                    System.out.println("Generated ID: " + customerId); // debug
                }

             
                try (PreparedStatement accountStmt = conn.prepareStatement(accountInsertSQL)) {
                    accountStmt.setInt(1, customerId);         // customer_id
                    accountStmt.setDouble(2, initialBalance);  // balance
                    accountStmt.executeUpdate();
                }
                // Commit the transaction
                conn.commit();
             
                System.out.println("Account created for " + customer.getName() + " successfully.");
            } catch (SQLException e) {
                
                System.out.println("Error occurred. Rolling back transaction...");
                 conn.rollback();
                 e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Task 2: Update customer details
    public static void updateCustomerDetails(Connection conn, Customer customer) {
        String updateSQL = "UPDATE customers SET address = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(updateSQL)) {
         
            stmt.setString(1, customer.getAddress());
            stmt.setInt(2, customer.getId());
         
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println(customer.getName() + " details updated successfully.");
            } else {
                System.out.println("Customer not found.");
            } conn.commit();

        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }

        }

    // Task 3: Delete a customer account
    public static void deleteCustomerAccount(Connection conn, int accountId) {
        
        String deleteAccountSQL = "DELETE FROM accounts WHERE id = ?";
        String deleteCustomerSQL = "DELETE FROM customers WHERE id = ? ";

        try {

            int customerId = getCustomerIdFromAccountId(conn, accountId);
        

                PreparedStatement deleteAccountStmt = conn.prepareStatement(deleteAccountSQL);
                deleteAccountStmt.setInt(1, accountId);
                deleteAccountStmt.executeUpdate();

            if (!hasOtherAccounts(conn, customerId)) {
                PreparedStatement deleteCustomerStmt = conn.prepareStatement(deleteCustomerSQL);
                deleteCustomerStmt.setInt(1, customerId);
                deleteCustomerStmt.executeUpdate();
            }
            // Commit the transaction
            conn.commit();
            System.out.println("Bank account belong to " + customerId + " deleted successfully.");

        } catch (SQLException e) {
            System.err.println("SQLException :" + e.getMessage());
        }
    }

    // Task 4: View all customers and their account details
    public static void viewAllCustomers(Connection conn) {
        
        String query = "SELECT customers.name, customers.address, accounts.id AS account_id, accounts.balance " +
                   "FROM customers " +
                   "JOIN accounts ON customers.id = accounts.customer_id";

    try {
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String name = rs.getString("name");
            String address = rs.getString("address");
            int accountId = rs.getInt("account_id");
            double balance = rs.getDouble("balance");

            System.out.printf("Customer: %s, Address: %s, Account: %d, Balance: %.2f%n",
                    name, address, accountId, balance);
        }

    } catch (SQLException e) {
        System.err.println("SQLException :" + e.getMessage());
    }
}
       


        

    // Helper method to get customer ID from account ID
    private static int getCustomerIdFromAccountId(Connection conn, int accountId) throws SQLException {
        String query = "SELECT customer_id FROM accounts WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("customer_id");
            }
        }
        return -1;
    }

    // Helper method to check if the customer has other accounts
    private static boolean hasOtherAccounts(Connection conn, int customerId) throws SQLException {
        String query = "SELECT COUNT(*) FROM accounts WHERE customer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // Method to create the Inventory Management database
    public static void createDatabase(Connection conn) throws SQLException {
        String query = "CREATE DATABASE IF NOT EXISTS BankDB";
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(query);
        System.out.println("Database 'BankDB' created successfully (if it didn’t exist).");
    }

    // Create tables for customers and accounts
    public static void createTables(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // use 'BankDB' database
            stmt.executeUpdate("USE BankDB");
            
            // eliminar tablas viejas
            stmt.executeUpdate("DROP TABLE IF EXISTS accounts");
            stmt.executeUpdate("DROP TABLE IF EXISTS customers");


            String createCustomersTableSQL = "CREATE TABLE IF NOT EXISTS customers ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "name VARCHAR(255),"
                    + "address VARCHAR(255))";

            String createAccountsTableSQL = "CREATE TABLE IF NOT EXISTS accounts ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "customer_id INT,"
                    + "balance DOUBLE,"
                    + "FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE)";

            // Create customers table
            stmt.execute(createCustomersTableSQL);

            // Create accounts table
            stmt.execute(createAccountsTableSQL);

            System.out.println("Tables created successfully.");

        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
    }

    public static Connection getDatabaseConnection() {
        String url = "jdbc:mysql://localhost:3306/";
        String user = "root";
        String password = "password";

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Connection failed SQLException: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println("Connection failed Exception: " + e.getMessage());
            return null;
        }
    }
}
