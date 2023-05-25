import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Date;

public class Main {
    private static final String x = "st";
    private static final String a = "Te" + x;
    private static final SecureRandom rand = new SecureRandom();

    public static void main(String[] args) {
        long start = System.nanoTime();
        for (int i=0; i < 10; i++) {
            System.out.println(a);
        }
        System.out.println();
        if (rand.nextBoolean()) {
            System.out.println("no way jump!");
        }
        System.out.println("Hello world!");

        for (TestEnum i : TestEnum.values()) {
            System.out.println(i.toString());
        }
        System.out.println("micros: " + (System.nanoTime() - start));
    }

    public static String encrypt(String value, int key) {
        int newKey = (value.length() * value.length()) ^ key;

        char k1 = (char) (newKey & 0xFFFF);
        char k2 = (char) ((newKey >> 16) & 0xFFFF);

        char[] chars = value.toCharArray();
        char[] output = new char[chars.length];

        for (int j = 0; j < chars.length; j++) {
            char i = chars[j];
            output[j] = (char) (i ^ k1 ^ k2);
        }
        return new String(output);
    }
}
