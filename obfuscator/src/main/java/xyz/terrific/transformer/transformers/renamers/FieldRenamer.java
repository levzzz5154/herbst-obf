package xyz.terrific.transformer.transformers.renamers;

import org.objectweb.asm.tree.ClassNode;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.transformer.Transformer;
import xyz.terrific.transformer.annotation.Group;
import xyz.terrific.util.Logger;
import xyz.terrific.util.RandomUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@Group(name = "renamers")
public class FieldRenamer extends Transformer {
    @Override
    public void transform() {
        Map<String, String> remap = new HashMap<>();

        classes.stream()
                .filter(clazz -> !isExcluded(clazz.name))
                .forEach(classNode ->
                        classNode.fields.forEach(field -> {
                            String name = RandomUtil.randomString();

                            Logger.getInstance().info((Object) "(%s.class) Renaming Field '%s' to '%s'", classNode.name, field.name, name);

                            Stack<ClassNode> stack = new Stack<>();
                            stack.add(classNode);

                            while (!stack.isEmpty()) {
                                ClassNode clazz = stack.pop();
                                remap.put(clazz.name + "." + field.name, name);

                                stack.addAll(getExtending(clazz));
                                stack.addAll(getImplementing(clazz));
                            }
                        }));

        applyRemap(remap);
    }

    @Override
    public boolean parseConfig(ConfigManager.Configs<String, Object> config) {
        RandomUtil.setAlphabet(config.safeGet("dictionary", RandomUtil.getAlphabet()));
        RandomUtil.setRandomLength(config.safeGet("length", RandomUtil.getRandomLength()));

        return config.safeGet("fields", true);
    }
}

