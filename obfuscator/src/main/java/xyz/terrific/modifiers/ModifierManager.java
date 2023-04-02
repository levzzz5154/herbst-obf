package xyz.terrific.modifiers;

import org.apache.bcel.generic.ClassGen;
import xyz.terrific.ClassObfuscator;
import xyz.terrific.util.JVM;
import xyz.terrific.util.Logger;

import java.lang.reflect.InvocationTargetException;
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

    public static void runModifiers(ClassGen classgen) {
        ModifierManager.getModifiers().forEach(modifier -> {
            try {
                if (ModifierManager.getShouldLog()) {
                    Logger.getInstance().info(ClassObfuscator.class, "Running " + modifier.getSimpleName());
                }
                modifier.getConstructor(ClassGen.class, Boolean.class)
                        .newInstance(classgen, false)
                        .transform();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                Logger.getInstance().error("Failed to call constructor of modifier '" + modifier.getSimpleName() + "' - " + e.getMessage());
            }
        });

    }

    public static List<Class<? extends Modifier>> getModifiers() {
        return modifiers;
    }

    public static boolean getShouldLog() {
        return shouldLog;
    }
}
