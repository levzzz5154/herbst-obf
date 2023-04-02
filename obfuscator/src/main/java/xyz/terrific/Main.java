package xyz.terrific;

import xyz.terrific.transformer.TransformerManager;
import xyz.terrific.obfuscator.ClassObfuscator;
import xyz.terrific.obfuscator.JarObfuscator;
import xyz.terrific.util.Logger;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;

public class Main {
    private static final String Version = "b0.0.1";

    private static TransformerManager modfierManager;


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

        Logger.getInstance().info("Starting: \n\t\tVersion: %s\n\t\tArguments: %s", Version, Arrays.toString(args));



        modfierManager = new TransformerManager(true);



        Arrays.stream(args)
                .map(File::new)
                .forEach(file -> {
                    if (!file.exists()) {
                        Logger.getInstance().error(Main.class, "file '" + file.getName() + "' doesnt exist");
                        return;
                    }

                    Logger.getInstance().info(Main.class, "Processing file: '%s'", file.getName());
                    if (file.getName().endsWith(".jar")) {
                        new JarObfuscator(file)
                                .obfuscate();
                    } else {
                        new ClassObfuscator(file)
                                .obfuscate();
                    }
                }
        );

        Logger.getInstance().info((Object) "Bye :)");
        Logger.getInstance().close();
    }


}
