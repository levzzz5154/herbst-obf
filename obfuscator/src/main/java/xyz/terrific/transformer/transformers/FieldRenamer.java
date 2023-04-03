package xyz.terrific.transformer.transformers;

import org.objectweb.asm.tree.ClassNode;
import xyz.terrific.transformer.Transformer;
import xyz.terrific.transformer.TransformerManager;
import xyz.terrific.util.Logger;
import xyz.terrific.util.RandomUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class FieldRenamer extends Transformer {
    public void transform() {
        Map<String, String> remap = new HashMap<>();

        classes
                .stream().filter(clazz -> clazz.name.startsWith("lol/november"))
                .forEach(classNode ->
                        classNode.fields.forEach(field -> {

                            String name = RandomUtil.generateRandomString(TransformerManager.getRandomLength());

                            Logger.getInstance().info((Object) "(%s.class) Renaming '%s' to '%s'", classNode.name, field.name, name);

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
}

