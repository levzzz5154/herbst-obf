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
import java.util.stream.Collectors;

public class TransformerManager {
    private static List<Transformer> transformers = new ArrayList<>();
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
            Logger.getInstance().error("Failed to load transformers");
        }
        reorderTransformers();
    }

    private static void reorderTransformers() {
        var orderList = (List<String>) Main.getConfigManager().getTransformerConfig().safeGet("order", null);
        if (orderList == null) {
            Logger.getInstance().info(TransformerManager.class.getSimpleName(), "No order specified");
            return;
        }
        transformers = transformers.stream()
                .sorted((o1, o2) -> {
                    if (o1.equals(o2)) return 0;
                    final var name1 = o1.getClass().getSimpleName().toLowerCase();
                    final var name2 = o2.getClass().getSimpleName().toLowerCase();
                    return orderList.indexOf(name1) - orderList.indexOf(name2);
                })
                .collect(Collectors.toList());

        Logger.getInstance().info(TransformerManager.class.getSimpleName(), "Specified order: ");
        for (int j = 0; j < transformers.size(); j++) {
            Transformer i = transformers.get(j);
            Logger.getInstance().info(TransformerManager.class.getSimpleName(), "#" + j + " " + i.getClass().getSimpleName());
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
            var mappedCfg = new ConfigManager.Configs<>((HashMap<String, Object>) Main.getConfigManager().getTransformerConfig().get(transformerConfig));
            if (group != null && !mappedCfg.safeGet("enabled", true)) {
                continue;
            }

            if (transformer.parseConfig(mappedCfg)) {
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
