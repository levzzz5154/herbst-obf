package xyz.terrific.transformer;

import xyz.terrific.Main;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.transformer.annotation.Exclude;
import xyz.terrific.transformer.annotation.Group;
import xyz.terrific.util.JVM;
import xyz.terrific.util.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransformerManager {
    private static final List<Transformer> transformers = new ArrayList<>();
    private static boolean shouldLog;

    public TransformerManager(boolean shouldLog) {
        TransformerManager.shouldLog = shouldLog;

        try {
            JVM.getAllClassesInPackage("xyz.terrific.transformer.transformers")
                    .forEach((clazz) -> {
                        if (!Transformer.class.isAssignableFrom(clazz) || clazz.isAnnotationPresent(Exclude.class)) {
                            return;
                        }

                        try {
                            transformers.add((Transformer) clazz.getConstructor().newInstance());
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            Logger.getInstance().error("Failed to get constructor (with signature: '()') for transformer '%s'", transformers.getClass().getName());
                        }
                    });
        } catch (Exception e) {
            Logger.getInstance().error("Failed to load modifiers");
        }
    }

    public TransformerManager() {
        this(false);
    }


    public static void runTransformers() {
        for (Transformer transformer : transformers()) {
            String transformerConfig = transformer.getClass().getSimpleName().toLowerCase();

            Group group = transformer.getClass().getAnnotation(Group.class);
            if (group != null) {
                transformerConfig = group.name().toLowerCase();
            }

            if (transformer.parseConfig(new ConfigManager.Configs<>((HashMap<String, Object>) Main.getConfigManager().getTransformerConfig().get(transformerConfig)))) {
                Logger.getInstance().info((Object) "Running %s", transformer.getClass().getSimpleName());
                transformer.transform();
            }
        }
    }


    public static List<? extends Transformer> transformers() {
        return transformers;
    }

    public static boolean getShouldLog() {
        return shouldLog;
    }
}
