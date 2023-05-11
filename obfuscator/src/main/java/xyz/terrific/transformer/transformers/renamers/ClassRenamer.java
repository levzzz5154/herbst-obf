package xyz.terrific.transformer.transformers.renamers;

import xyz.terrific.JarObfuscator;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.transformer.Transformer;
import xyz.terrific.transformer.annotation.Group;
import xyz.terrific.util.ClassNodeUtils;
import xyz.terrific.util.Logger;
import xyz.terrific.util.RandomUtil;

import java.util.HashMap;
import java.util.Map;

@Group(name = "renamers")
public class ClassRenamer extends Transformer {
    @Override
    public void transform() {
        Map<String, String> remap = new HashMap<>();

        classes.stream()
                .filter(clazz -> !isExcluded(clazz.name))
                .forEach(classNode -> {
                    String name = RandomUtil.randomString();
                    remap.put(classNode.name, name);

                    Logger.getInstance().info((Object) "Renaming class '%s' to '%s'", ClassNodeUtils.getClassName(classNode.name), name);

                    if (JarObfuscator.getManifest() != null && JarObfuscator.getManifest().getMainAttributes().getValue("Main-Class").equals(classNode.name.replace("/", "."))) {
                        JarObfuscator.getManifest().getMainAttributes().putValue("Main-Class", name.replace("/", "."));
                    }
                });

        applyRemap(remap);
    }

    @Override
    public boolean parseConfig(ConfigManager.Configs<String, Object> config) {
        RandomUtil.setAlphabet(config.safeGet("dictionary", RandomUtil.getAlphabet()));
        RandomUtil.setRandomLength(config.safeGet("length", RandomUtil.getRandomLength()));

        return config.safeGet("classes", true);
    }
}
