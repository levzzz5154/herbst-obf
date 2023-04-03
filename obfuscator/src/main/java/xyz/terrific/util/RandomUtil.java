package xyz.terrific.util;

import java.security.SecureRandom;

public class RandomUtil {
    private static final SecureRandom random = new SecureRandom();
    private static String alphabet = "abcdefghijklmnopqrstuvwxyzüöäABCDEFGHIJKLMNOPQRSTUVWXYZÜÖÄ";
    private static int randomLength = 12;


    public static String generateRandomString() {
        return generateRandomString(getRandomLength());
    }

    public static String generateRandomString(int length) {
        StringBuilder result = new StringBuilder();
        for (int i=0; i < length+1; i++) {
            result.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return result.toString();
    }

    public static int getRandomLength() {
        return randomLength;
    }

    public static void setRandomLength(Integer length) {
        if (length != null && length >= 1) {
            randomLength = length;
        }
    }

    public static String getAlphabet() {
        return alphabet;
    }

    public static void setAlphabet(String dictionary) {
        if (dictionary != null && !dictionary.isEmpty()) {
            alphabet = dictionary;
        }
    }
}
