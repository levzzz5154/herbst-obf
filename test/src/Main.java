import java.security.SecureRandom;

public class Main {
    private static final String x = "st ";
    private static final String a = "Te" + x;

    private static float aFloat = 2342.523f;
    private static double aDouble = 62347.1235d;
    private static long aLong = 1234123412341234123L;

    private static final String someTestString = encrypt(new char[] {'h', 'e', 'l', 'l', 'o'}, 3421);
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
        System.out.println("float " + aFloat);
        System.out.println("double " + aDouble);
        System.out.println("long " + aLong);
        aDouble = aDouble + Math.abs(67.89d);

        for (TestEnum i : TestEnum.values()) {
            System.out.println(i.toString());
        }
        System.out.println("micros: " + (System.nanoTime() - start));
        var sus = new Main();
        sus.testingOfANonStaticMeth(43);
    }

    public static String encrypt(char[] chars, int key) {
        int newKey = (chars.length * chars.length) ^ key;

        char k1 = (char) (newKey & 0xFFFF);
        char k2 = (char) ((newKey >> 16) & 0xFFFF);

        char[] output = new char[chars.length];

        for (int j = 0; j < chars.length; j++) {
            char i = chars[j];
            output[j] = (char) (i ^ k1 ^ k2);
        }
        return new String(output);
    }

    void testingOfANonStaticMeth(int local1) {
        System.out.println(this.getClass().getName() + local1);
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
