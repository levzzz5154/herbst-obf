package xyz.terrific.modifiers;

import xyz.terrific.util.JVM;
import xyz.terrific.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class ModifierManager {
    private static final List<Class<? extends Modifier>> modifiers = new ArrayList<>();
    private static boolean shouldLog;

    public ModifierManager(boolean shouldLog) {
        ModifierManager.shouldLog = shouldLog;

        try {
            JVM.getAllClassesInPackage("xyz.terrific.modifiers.modifiers")
                    .forEach((clazz) -> {
                        if (!Modifier.class.isAssignableFrom(clazz)) {
                            return;
                        }

                        modifiers.add((Class<? extends Modifier>) clazz);
                    });
        } catch (Exception e) {
            Logger.getInstance().error("Failed to load modifiers");
        }
    }

    public ModifierManager() {
        this(false);
    }

    public static List<Class<? extends Modifier>> getModifiers() {
        return modifiers;
    }

    public static boolean getShouldLog() {
        return shouldLog;
    }
}
