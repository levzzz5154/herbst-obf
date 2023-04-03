package xyz.terrific;

import xyz.terrific.config.ConfigManager;
import xyz.terrific.transformer.TransformerManager;
import xyz.terrific.util.Logger;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;

public class Main {
    private static final String Name    = "Herbst";
    private static final String Version = "b0.0.1";

    private static TransformerManager transformerManager;
    private static ConfigManager configManager;


    public static void main(String[] args) {
        if (args.length < 1) {
            try {
                System.out.printf("Usage: %s [Files...]\n", new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getName());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            exit(1);
        }

        if (!Logger.getInstance().initialize(true, false)) {
            System.err.println("Logger failed to initialize");
            exit(1);
        }

        Logger.getInstance().raw("%s Starting: \n\t\tVersion: %s\n\t\tArguments: %s", Name, Version, Arrays.toString(args));

        configManager = new ConfigManager(new File(args[0]));
        configManager.load();

        File file = new File((String) configManager.getConfig().get("input"));
        Logger.getInstance().info(Main.class, "Processing file: '%s'", file.getName());
        new JarObfuscator(file).init();
        transformerManager = new TransformerManager(true);
        new JarObfuscator(file).obfuscate();

        exit(0);
    }

    public static String getName() {
        return Name;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static void exit(int i) {
        Logger.getInstance().raw("Bye :)");
        Logger.getInstance().close();
        System.exit(i);
    }
}
