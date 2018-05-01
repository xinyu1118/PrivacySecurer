package io.github.privacysecurer.utils;

/**
 * Some communication-related utility functions
 */

public class CommunicationUtils {
    public static String normalizePhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll("[^a-zA-Z0-9]", "");
    }
}
