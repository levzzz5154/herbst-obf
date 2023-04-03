package xyz.terrific.util;

import java.security.SecureRandom;

public class RandomUtil {
    private static final SecureRandom random = new SecureRandom();
    private static final String alphabet = "abcdefghijklmnopqrstuvwxyzüöäABCDEFGHIJKLMNOPQRSTUVWXYZÜÖÄ";
    private static final int randomLength = 12;


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
}
