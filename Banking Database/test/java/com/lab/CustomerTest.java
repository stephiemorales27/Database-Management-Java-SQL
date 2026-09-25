package com.lab;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CustomerTest {
    @Test
    public void constructorAndSettersPreserveCustomerData() {
        Customer customer = new Customer(3, "Jane Smith", "456 Oak St");

        assertEquals(3, customer.getId());
        assertEquals("Jane Smith", customer.getName());
        assertEquals("456 Oak St", customer.getAddress());

        customer.setId(4);
        customer.setAddress("789 Pine St");
        assertEquals(4, customer.getId());
        assertEquals("789 Pine St", customer.getAddress());
    }
}
