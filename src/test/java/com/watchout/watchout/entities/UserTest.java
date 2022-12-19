package com.watchout.watchout.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    User user = new User();
    @Test
    void testGetId() {
        user.setId(77);
        int expected = 77;
        int actual = user.getId();
        assertEquals(expected, actual);
    }

    @Test
    void testGetName() {
        user.setName("Ruhan Mahir");
        String expected = "Ruhan Mahir";
        String actual = user.getName();
        assertEquals(expected, actual);
    }

    @Test
    void testGetEmail() {
        user.setEmail("ruhan@mahir.com");
        String expected = "ruhan@mahir.com";
        String actual = user.getEmail();
        assertEquals(expected, actual);
    }

    @Test
    void testGetPassword() {
        user.setPassword("RuhanPassword");
        String expected = "RuhanPassword";
        String actual = user.getPassword();
        assertEquals(expected, actual);
    }

    @Test
    void testGetRole() {
        user.setRole("Admin");
        String expected = "Admin";
        String actual = user.getRole();
        assertEquals(expected, actual);
    }
}