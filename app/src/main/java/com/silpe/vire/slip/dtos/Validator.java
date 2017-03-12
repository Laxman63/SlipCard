package com.silpe.vire.slip.dtos;

/**
 * A centralized validation class for input fields.
 * This class ensures consistent validation methods
 * across the app.
 */
public class Validator {

    public static boolean isValidEmail(String email) {
        // TODO Provide proper email validation
        return email.contains("@");
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        // TODO Provide proper phone number validation
        return !phoneNumber.matches(".*[a-zA-Z].*");
    }

    public static boolean isValidPassword(String password) {
        // TODO Provide more rigorous password validation
        return password.length() > 4;
    }

}
