package xyz.terrific.transformer;

import xyz.terrific.util.JVM;
import xyz.terrific.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class TransformerManager {
    private static final List<Class<? extends Transformer>> modifiers = new ArrayList<>();
    private static boolean shouldLog;
    private static int randomLength = 12;

    public TransformerManager(boolean shouldLog) {
        TransformerManager.shouldLog = shouldLog;

        try {
            JVM.getAllClassesInPackage("xyz.terrific.modifiers.modifiers")
                    .forEach((clazz) -> {
                        if (!Transformer.class.isAssignableFrom(clazz)) {
                            return;
                        }

                        modifiers.add((Class<? extends Transformer>) clazz);
                    });
        } catch (Exception e) {
            Logger.getInstance().error("Failed to load modifiers");
        }
    }

    public TransformerManager() {
        this(false);
    }

    public static void runModifiers() {
        TransformerManager.getModifiers().forEach(modifier -> {
            // run modifiers
        });

    }

    public static List<Class<? extends Transformer>> getModifiers() {
        return modifiers;
    }

    public static boolean getShouldLog() {
        return shouldLog;
    }

    public static int getRandomLength() {
        return randomLength;
    }
}
