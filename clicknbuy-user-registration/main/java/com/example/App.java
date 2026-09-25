package com.example;
import java.sql.SQLException;

public class App { 
    public App() {
    }

    public static void main( String[] args ) throws ClassNotFoundException, SQLException {
    
     User newUser = new User("betty.george" + System.currentTimeMillis(),
                        "betty.george" + System.currentTimeMillis() + "@email.com",
                        "Betty",
                        "George",
                        "+11536549870",
                        15000);
    
    UserDAO userDAO = new UserDAO();

        
    try { 
        int rowsUpdated = userDAO.addUser(newUser);
        if (rowsUpdated== 1) {
            System.out.println("New User added successfully");
            
        } else {
            System.out.println("Cannot add user");
        }
    } catch(ClassNotFoundException var4) {
        var4.printStackTrace();
    } catch (SQLException var5) {
        var5.printStackTrace();

    }
    } 
}
