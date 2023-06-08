package xyz.terrific.transformer.transformers.shuffler;

import org.objectweb.asm.tree.FieldNode;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.transformer.Transformer;
import xyz.terrific.transformer.annotation.Group;

import java.util.Collections;
import java.util.List;

@Group(name = "shuffler")
public class FieldShuffler extends Transformer {
    @Override
    public void transform() {
        classes.stream().filter(c -> !isExcluded(c.name))
                .forEach(classNode -> {
                    shuffleFields(classNode.fields);
                });
    }
    @Override
    public boolean parseConfig(ConfigManager.Configs<String, Object> config) {
        return config.safeGet("fields", false);
    }

    public static void shuffleFields(List<FieldNode> fields) {
        Collections.shuffle(fields);
    }
}
