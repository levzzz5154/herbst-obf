package xyz.terrific.config;


import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import xyz.terrific.Main;
import xyz.terrific.util.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class ConfigManager {
    private File configFile;
    private Map<String, Object> transformerConfig;
    private Map<String, Object> config;


    public ConfigManager(File configFile) {
        this.configFile = configFile;

        if (!configFile.getName().endsWith(".yml") && !configFile.getName().endsWith(".cfg") && !configFile.getName().endsWith(".yaml")) {
            Logger.getInstance().error((String) "Extension for Config File '%s' (%s) is not supported", configFile.getName(), configFile.getName().substring(configFile.getName().lastIndexOf(".")));
            Main.exit(1);
        }

        if (!configFile.exists()) {
            Logger.getInstance().error((String) "Config File '%s' doesnt exist", configFile.getName());
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
            config = yaml.load(Files.newInputStream(configFile.toPath()));
            transformerConfig = (Map<String, Object>) config.get("transformers");
        } catch (IOException e) {
            Logger.getInstance().error((Object) "Failed to load config file '%s' - %s", configFile.getName(), e.getMessage());
            Main.exit(1);
        }
    }


    public Map<String, Object> getConfig() {
        return config;
    }

    public Map<String, Object> getTransformerConfig() {
        return transformerConfig;
    }
}
