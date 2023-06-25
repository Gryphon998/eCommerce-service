package com.ecommerce.common;

// This class defines constants used in the application
public class Const {

    // Role interface defines the roles of users in the system
    public interface Role {
        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
    }

    // Constant for storing the currently logged-in user
    public static final String CURRENT_USER = "currentUser";

    // Constant for storing email information
    public static final String EMAIL = "email";

    // Constant for storing username information
    public static final String USERNAME = "username";
}