import java.security.SecureRandom;

public class Main {
    private static final String x = "st";
    private static final String a = "Te" + x;
    private static final SecureRandom rand = new SecureRandom();

    public static void main(String[] args) {
        

        for (int i=0; i < 10; i++) {
            System.out.println(a);
        }

        System.out.println();
        if (rand.nextBoolean()) {
            System.out.println("no way jump!");
        }
        System.out.println("Hello world!");
    }
}
