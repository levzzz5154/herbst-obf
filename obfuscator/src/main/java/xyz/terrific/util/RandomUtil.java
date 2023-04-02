package xyz.terrific.util;

import java.security.SecureRandom;

public class RandomUtil {
    private static final SecureRandom random = new SecureRandom();
    private static final String alphabet = "abcdefghijklmnopqrstuvwxyzüöäABCDEFGHIJKLMNOPQRSTUVWXYZÜÖÄ";


    public static String generateRandomString() {
        return generateRandomString(random.nextInt() * 100);
    }

    public static String generateRandomString(int length) {
        StringBuilder result = new StringBuilder();
        for (int i=0; i < length+1; i++) {
            result.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return result.toString();
    }

}
