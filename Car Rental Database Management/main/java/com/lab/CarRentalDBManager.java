
package com.lab;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class CarRentalDBManager {

    public static void main(String[] args) {
        Connection conn = null;
        try {
            // establish the connection to MySQL
            conn = getDatabaseConnection();
            // check connection is not null before using it
            if (conn != null) {

                // create the database and tables
                CarRentalDBManager.createDatabase(conn);
                CarRentalDBManager.createTables(conn);

                   // limpiar tablas antes de insertar
             Statement cleanStmt = conn.createStatement();
             cleanStmt.executeUpdate("DELETE FROM customers");
             cleanStmt.executeUpdate("DELETE FROM cars");
             cleanStmt.executeUpdate("ALTER TABLE cars AUTO_INCREMENT = 1");
             cleanStmt.executeUpdate("ALTER TABLE customers AUTO_INCREMENT = 1");

                // add a new car and associate a customer with it
                CarRentalDBManager.addCarAndCustomer(conn);

                // update the car's availability and remove the customer
                CarRentalDBManager.updateCarAndRemoveCustomer(conn);

                // fetch all available cars and display their details
                CarRentalDBManager.fetchAllCarsAndStatus(conn);

            } else {
                System.err.println("Failed to establish a connection to the database.");
            }
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        } finally {
            // close the connection if it was successfully opened
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("Database connection closed.");
                } catch (SQLException e) {
                    System.err.println("Error closing the database connection: " + e.getMessage());
                }
            }
        }
    }

    // Task 1: Adding a new car and customer
    public static void addCarAndCustomer(Connection conn) {

        PreparedStatement pstmt = null;
        PreparedStatement customerPstmt = null;
        ResultSet generatedKeys = null;

        try {
    
            String carQuery = "INSERT INTO cars (model, year, availability) VALUES (?, ?, ?)";
            
            pstmt = conn.prepareStatement(carQuery, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, "Toyota Camry");  // model
            pstmt.setInt(2, 2021);               // year
            pstmt.setBoolean(3, false);          // availability
            pstmt.executeUpdate();               // execute
    
            // retrieve the generated car_id from the ResultSet after executing the update
            generatedKeys = pstmt.getGeneratedKeys();
            int carId = 0;
            if (generatedKeys.next()) {
                // retrieve the car_id
                carId = generatedKeys.getInt(1);
            }
        
            String customerQuery = "INSERT INTO customers (name, car_id) VALUES (?, ?)";
            
            customerPstmt = conn.prepareStatement(customerQuery);
            customerPstmt.setString(1, "John Doe");
            customerPstmt.setInt(2, carId);
            customerPstmt.executeUpdate();

            // print success messages confirming that the car and customer were successfully added
            System.out.println("Car added successfully: Toyota Camry (2021)");
            System.out.println("Customer 'John Doe' associated with car ID: " + carId);

        } catch (SQLException e) {
            // handle SQL exception
            System.out.println("Error occurred while adding car or customer: " + e.getMessage());
        } finally {
            // close resources in the finally block to avoid resource leaks
            try {
                if (generatedKeys != null) {
                    generatedKeys.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (customerPstmt != null) {
                    customerPstmt.close();
                }
            } catch (SQLException e) {
                System.out.println("Error occurred while closing resources: " + e.getMessage());
            }
        }
    }

    // Task2: Updating a car's availability and removing the customer
    public static void updateCarAndRemoveCustomer(Connection conn) throws SQLException {
        
        String updateCarQuery = "UPDATE cars SET availability =  true WHERE car_id = 1";

        
        PreparedStatement pstmt = conn.prepareStatement(updateCarQuery);
        pstmt.executeUpdate();

        String deleteCustomerQuery = "DELETE FROM customers WHERE car_id = 1";

        PreparedStatement deletePstmt = conn.prepareStatement(deleteCustomerQuery);
        deletePstmt.executeUpdate();

        // print success messages
        System.out.println("Car availability updated: Toyota Camry is now available.");
        System.out.println("Customer 'John Doe' removed from the system.");
    }

    // Task 3: Fetching all available cars and status
    public static void fetchAllCarsAndStatus(Connection conn) {
        Statement stmt = null;
        ResultSet rs = null;

        try {
                String selectQuery = "SELECT cars.model, customers.name "
                    + "FROM cars "
                    + "LEFT JOIN customers ON cars.car_id = customers.car_id ";

            
            stmt = conn.createStatement();
            rs = stmt.executeQuery(selectQuery);
           

            while (rs.next()) {

                String model = rs.getString("model");

                String customerName = rs.getString("name");

                 // print car model
            System.out.println("Car Model: " + model);

                // check if rented or available
            if (customerName != null) {
                System.out.println("Rented by: " + customerName);
            } else {
                System.out.println("Available for rent.");
            }
        }


            // print a separator for clarity
            System.out.println("----------------------");
        } catch (SQLException e) {
            // Handle SQL exception
            System.out.println("Error occurred while fetching available cars and customers: " + e.getMessage());
        } finally {
            // Close resources in the finally block to avoid resource leaks
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                System.out.println("Error occurred while closing resources: " + e.getMessage());
            }
        }
    }

    // Method to create the CarRentalDB database
    public static void createDatabase(Connection conn) throws SQLException {
        String query = "CREATE DATABASE IF NOT EXISTS CarRentalDB";
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(query);
        System.out.println("Database 'CarRentalDB' created successfully (if it didn’t exist).");
    }

    // Method to create the 'cars' and 'customers' tables
    public static void createTables(Connection conn) throws SQLException {
        // switch to the CarRentalDB database
        String useDBQuery = "USE CarRentalDB";
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(useDBQuery);

        // create 'cars' table
        String createCarsTable = "CREATE TABLE IF NOT EXISTS cars ("
                + "car_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "model VARCHAR(100), "
                + "year INT, "
                + "availability BOOLEAN)";
        stmt.executeUpdate(createCarsTable);
        System.out.println("Table 'cars' created successfully.");

        // create 'customers' table with a foreign key referencing 'car_id'
        String createCustomersTable = "CREATE TABLE IF NOT EXISTS customers ("
                + "customer_id INT AUTO_INCREMENT PRIMARY KEY, "
                + "name VARCHAR(100), "
                + "car_id INT, "
                + "FOREIGN KEY (car_id) REFERENCES cars(car_id))";
        stmt.executeUpdate(createCustomersTable);
        System.out.println("Table 'customers' created successfully.");
    }

    // Establishing Connection:
    private static Connection getDatabaseConnection() {
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
