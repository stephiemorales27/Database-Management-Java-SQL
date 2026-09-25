package com.example.database;

import com.mysql.cj.jdbc.MysqlDataSource;

public class ConnectionHelper {

    // an instance to the MysqlDataSource is already created, you only need to initialize its properties to connect to the MySQL database
    private MysqlDataSource mysqlDatabase;
    

    public MysqlDataSource getDataSource() {
        return this.mysqlDatabase;
    }

    // complete the connect() method to connect to the MySQL database
    public void connect(String databaseURL, String username, String password) {
       
            mysqlDatabase = new MysqlDataSource();
        
        mysqlDatabase.setUrl(databaseURL);
       
        mysqlDatabase.setUser(username);
        
        // The password should be 'password'
        mysqlDatabase.setPassword(password);
    }
}
