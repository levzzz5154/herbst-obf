package xyz.terrific;

import xyz.terrific.obfuscator.JarObfuscator;
import xyz.terrific.transformer.TransformerManager;
import xyz.terrific.util.Logger;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;

public class Main {
    private static final String Name    = "Herbst";
    private static final String Version = "b0.0.1";

    private static TransformerManager tManager;


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

        Logger.getInstance().raw("%s Starting: \n\t\tVersion: %s\n\t\tArguments: %s", Name, Version, Arrays.toString(args));

//        Arrays.stream(args)
//                .map(File::new)
//                .forEach(file -> {
//                    if (!file.exists()) {
//                        Logger.getInstance().error(Main.class, "file '" + file.getName() + "' doesnt exist");
//                        return;
//                    }
//
//                    Logger.getInstance().info(Main.class, "Processing file: '%s'", file.getName());
//                    new JarObfuscator(file).init();
//                    tManager = new TransformerManager(false);
//                    new JarObfuscator(file).obfuscate();
//                }
//        );

        File file = new File(args[0]);
        Logger.getInstance().info(Main.class, "Processing file: '%s'", file.getName());
        new JarObfuscator(file).init();
        tManager = new TransformerManager(true);
        new JarObfuscator(file).obfuscate();

        Logger.getInstance().raw("Bye :)");
        Logger.getInstance().close();
    }

    public static String getName() {
        return Name;
    }
}
