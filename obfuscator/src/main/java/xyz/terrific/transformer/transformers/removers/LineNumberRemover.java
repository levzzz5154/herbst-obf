package xyz.terrific.transformer.transformers.removers;

import org.objectweb.asm.tree.LineNumberNode;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.transformer.Transformer;
import xyz.terrific.transformer.annotation.Group;

@Group(name = "removers")
public class LineNumberRemover extends Transformer {
    @Override
    public void transform() {
        classes.stream().filter(classNode -> !isExcluded(classNode.name)).forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                methodNode.instructions.forEach(insnNode -> {
                    if (insnNode instanceof LineNumberNode) {
                        methodNode.instructions.remove(insnNode);
                    }
                });
            });
        });
    }

    @Override
    public boolean parseConfig(ConfigManager.Configs<String, Object> config) {
        return config.safeGet("lineNumbers", false);
    }
}
