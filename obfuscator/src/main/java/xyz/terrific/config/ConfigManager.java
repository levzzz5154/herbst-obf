package xyz.terrific.config;


import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import xyz.terrific.Main;
import xyz.terrific.util.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigManager {
    private final File configFile;
    private Configs<String, Object> transformerConfig;
    private Configs<String, Object> config;


    public ConfigManager(File configFile) {
        this.configFile = configFile;

        if (!configFile.getName().endsWith(".yml") && !configFile.getName().endsWith(".cfg") && !configFile.getName().endsWith(".yaml")) {
            Logger.getInstance().error((Object) "Extension for Config File '%s' (%s) is not supported", configFile.getName(), configFile.getName().substring(configFile.getName().lastIndexOf(".")));
            Main.exit(1);
        }

        if (!configFile.exists()) {
            Logger.getInstance().error((Object) "Config File '%s' doesnt exist", configFile.getName());
            Main.exit(1);
        }
    }

    public void load() {
        if (configFile == null) {
            Logger.getInstance().error((Object) "ConfigFile is null");
            Main.exit(1);
        }

        LoaderOptions loaderoptions = new LoaderOptions();
        loaderoptions.setAllowRecursiveKeys(true);

        try {
            Yaml yaml = new Yaml(loaderoptions);
            config = new Configs<>(yaml.load(Files.newInputStream(Objects.requireNonNull(configFile).toPath())));
            transformerConfig = new Configs<>((HashMap<String, Object>) config.get("transformers"));
        } catch (IOException e) {
            Logger.getInstance().error((Object) "Failed to load config file '%s' - %s", Objects.requireNonNull(configFile).getName(), e.getMessage());
            Main.exit(1);
        }
    }


    public Configs<String, Object> getConfig() {
        return config;
    }

    public Configs<String, Object> getTransformerConfig() {
        return transformerConfig;
    }


    public static class Configs<K, V> extends HashMap<K, V> implements Map<K, V> {
        public Configs(Map<K, V> map) {
            super(map);
        }
        public Configs(HashMap<K, V> map) {
            super(map);
        }
        public Configs(LinkedHashMap<K, V> map) {
            super(map);
        }

        /**
         * @param key key to get from map
         * @param defaultVal default value to be returned when result of `get(key)` is null
         * @return returns get(key) or default value (when get(key) is null)
         * @param <T> type of default value and return value
         */
        public <T> T safeGet(String key, T defaultVal) {
            T value = (T) this.get(key);
            return value == null ? defaultVal : value;
        }
    }
}
