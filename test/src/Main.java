import java.security.SecureRandom;

public class Main {
    private static final String x = "st ";
    private static final String a = "Te" + x;

    private static final float aFloat = 2342.523f;
    private static final double aDouble = 62347.1235d;
    private static final long aLong = 1234123412341234123L;

    private static final String someTestString = encrypt("fasdfh", 3421);
    private static final SecureRandom rand = new SecureRandom();

    public static void main(String[] args) {
        long start = System.nanoTime();
        for (int i=0; i < 10; i++) {
            System.out.println(a + i);
        }
        System.out.println();
        if (rand.nextBoolean()) {
            System.out.println("no way jump!");
        }
        System.out.println("Hello world!");
        System.out.println("float " + aFloat);
        System.out.println("double " + aDouble);
        System.out.println("long " + aLong);

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

    static int encDecInt(int value, int key) {
        return value ^ key;
    }
    static long encDecLong(long value, long key) {
        return value ^ key;
    }
    static short encDecShort(short value, short key) {
        return (short) (value ^ key);
    }
    static int encryptFloat(float value, int key) {
        return Float.floatToRawIntBits(value) ^ key;
    }
    static float decryptFloat(int value, int key) {
        return Float.intBitsToFloat(value ^ key);
    }
    static long encryptDouble(double value, long key) {
        return Double.doubleToRawLongBits(value) ^ key;
    }
    static double decryptDouble(long value, long key) {
        return Double.longBitsToDouble(value ^ key);
    }
}
