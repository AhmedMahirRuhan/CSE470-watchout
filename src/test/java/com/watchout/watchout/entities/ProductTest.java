package com.watchout.watchout.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    Product p = new Product();
    @Test
    void testGetId() {
        p.setId(999);
        int expected = 999;
        int actual = p.getId();
        assertEquals(expected, actual);
    }

    @Test
    void testGetDescription() {
        p.setDescription("This is a demo description for testing ....");
        String expected = "This is a demo description for testing ....";
        String actual = p.getDescription();
        assertEquals(expected, actual);
    }

    @Test
    void testGetPrice() {
        p.setPrice(299);
        int expected = 299;
        int actual = p.getPrice();
        assertEquals(expected, actual);
    }

    @Test
    void testGetQuantity() {
        p.setQuantity(7);
        int expected = 7;
        int actual = p.getQuantity();
        assertEquals(expected, actual);
    }
}