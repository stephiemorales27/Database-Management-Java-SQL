package com.example;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class UserTest {
    @Test
    public void constructorAndAccessorsPreserveUserData() {
        User user = new User("jane", "jane@example.com", "Jane", "Doe", "555-0100", 42);

        assertEquals("jane", user.getUserName());
        assertEquals("jane@example.com", user.getUserEmail());
        assertEquals("Jane", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("555-0100", user.getPhoneNumber());
        assertEquals(42, user.getRewardPoints());
    }

    @Test
    public void settersUpdateAllMutableFields() {
        User user = new User();
        user.setUserId(7);
        user.setUserName("updated");
        user.setUserEmail("updated@example.com");
        user.setFirstName("Updated");
        user.setLastName("User");
        user.setPhoneNumber("555-0199");
        user.setRewardPoints(99);

        assertEquals(7, user.getUserId());
        assertEquals("updated", user.getUserName());
        assertEquals("updated@example.com", user.getUserEmail());
        assertEquals("Updated", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals("555-0199", user.getPhoneNumber());
        assertEquals(99, user.getRewardPoints());
    }

    @Test
    public void toStringIncludesIdentityAndContactFields() {
        User user = new User("jane", "jane@example.com", "Jane", "Doe", "555-0100", 42);
        user.setUserId(7);

        String representation = user.toString();
        org.junit.Assert.assertTrue(representation.contains("userId=7"));
        org.junit.Assert.assertTrue(representation.contains("userName='jane'"));
        org.junit.Assert.assertTrue(representation.contains("rewardPoints=42"));
    }
}
