package xyz.terrific.transformer.transformers;

import xyz.terrific.JarObfuscator;
import xyz.terrific.transformer.Transformer;
import xyz.terrific.util.ClassNodeUtils;
import xyz.terrific.util.Logger;
import xyz.terrific.util.RandomUtil;

import java.util.HashMap;
import java.util.Map;

public class ClassRenamer extends Transformer {
    @Override
    public void transform() {
        Map<String, String> remap = new HashMap<>();

        classes.stream()
                .filter(clazz -> !isExcluded(clazz.name))
                .forEach(classNode -> {
                    String name = RandomUtil.generateRandomString();
                    remap.put(classNode.name, name);

                    Logger.getInstance().info((Object) "Renaming class '%s' to '%s'", ClassNodeUtils.getClassName(classNode.name), name);

                    if (JarObfuscator.getManifest() != null && JarObfuscator.getManifest().getMainAttributes().getValue("Main-Class").equals(classNode.name.replace("/", "."))) {
                        JarObfuscator.getManifest().getMainAttributes().putValue("Main-Class", name.replace("/", "."));
                    }
                });

        applyRemap(remap);
    }
}
