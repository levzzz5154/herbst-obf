package xyz.terrific.transformer.transformers;

import org.objectweb.asm.tree.ClassNode;
import xyz.terrific.Main;
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
                            String name = RandomUtil.generateRandomString();

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
    public boolean parseConfig(Map<String, Object> config) {
        RandomUtil.setAlphabet((String) config.get("dictionary"));
        RandomUtil.setRandomLength((Integer) config.get("length"));

        return (Boolean) config.get("fields");
    }
}

