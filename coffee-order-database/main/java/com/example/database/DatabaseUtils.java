package com.example.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.example.model.Customer;

public class DatabaseUtils {

    private ConnectionHelper mysqlDatabaseConnectionHelper;

    public DatabaseUtils(String databaseURL, String username, String password) {
        // create an instance of the ConnectionHelper class
        this.mysqlDatabaseConnectionHelper = new ConnectionHelper();
        // connect to the MySQL database using the provided databaseURL, username, and password
        this.mysqlDatabaseConnectionHelper.connect(databaseURL, username, password);
    }

    public List<Customer> getAllCustomers() {

        
        List<Customer> customers = new ArrayList<Customer>();

        try (
           
            Connection mysqlDatabaseConnection = mysqlDatabaseConnectionHelper.getDataSource().getConnection();
         
             Statement sqlStatement = mysqlDatabaseConnection.createStatement();
            
            ResultSet fetchAllCustomersQueryResultSet = sqlStatement.executeQuery("SELECT * FROM coffee_orders;");
        ) {

           // while the result set has more rows
           while (fetchAllCustomersQueryResultSet.next()) {

            long orderID = fetchAllCustomersQueryResultSet.getLong("order_id");
           
            String customerName = fetchAllCustomersQueryResultSet.getString("customer_name");

            int quantity = fetchAllCustomersQueryResultSet.getInt("quantity");

            double totalPrice = fetchAllCustomersQueryResultSet.getDouble("total_price");   

        
            Customer customer = new Customer(orderID, customerName, quantity, totalPrice);

            customers.add(customer);
            
        }

        } catch (SQLException exception) {
            // print an error message on the console
            System.err.println("Error fetching customers from the database");
            System.err.println(exception.getMessage());
            exception.printStackTrace();
        }
        
        // return the list of customers fetched from the database
        return customers;
    }
}
