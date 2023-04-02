package xyz.terrific;

import xyz.terrific.util.Logger;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;

public class Main {
    private static final String Version = "b0.0.1";


    public static void main(String[] args) {
        if (args.length < 1) {
            try {
                System.out.printf("Usage: %s [Files...]\n", new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getName());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            System.exit(1);
        }

        if (!Logger.getInstance().initialize(true, false)) {
            System.err.println("Logger failed to initialize");
            System.exit(1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Logger.getInstance().info("Bye :)");
            Logger.getInstance().close();
        }));
        Logger.getInstance().info(String.format("Starting: \n\t\tVersion: %s\n\t\tArguments: %s", Version, Arrays.toString(args)));

    }


}
