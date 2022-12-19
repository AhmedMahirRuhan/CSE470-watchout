package com.watchout.watchout.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    Order order = new Order();
    @Test
    void testGetStatus() {
        order.setStatus("Testing Status");
        String expected = "Testing Status";
        String actual = order.getStatus();
        assertEquals(expected, actual);
    }

    @Test
    void testGetAddress() {
        order.setAddress("Testing Address");
        String expected = "Testing Address";
        String actual = order.getAddress();
        assertEquals(expected, actual, "Address Success");
    }

    @Test
    void testGetNumber() {
        order.setNumber("+880123456789");
        String expected = "+880123456789";
        String actual = order.getNumber();
        assertEquals(expected, actual);
    }

    @Test
    void testGetName() {
        order.setName("Ruhan Mahir");
        String expected = "Ruhan Mahir";
        String actual = order.getName();
        assertEquals(expected, actual);
    }
}