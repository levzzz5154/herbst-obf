package xyz.terrific.transformer.transformers.flow;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LdcInsnNode;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.transformer.Transformer;
import xyz.terrific.transformer.annotation.Exclude;
import xyz.terrific.transformer.annotation.Group;
import xyz.terrific.util.Logger;

@Group(name = "flow")
public class FieldValueExtractor extends Transformer {
    @Override
    public void transform() {
        classes.stream().filter(classNode -> !isExcluded(classNode.name)).forEach(classNode -> {
            var clMethod = classNode.methods.stream()
                    .filter(methodNode -> methodNode.name.equals("<clinit>")
                            && methodNode.desc.equals("()V")
                    ).findFirst();

            if (clMethod.isPresent()) {
                final var clinitMethod = clMethod.get();
                var count = 0;
                for (FieldNode fieldNode : classNode.fields) {
                    if (fieldNode.value == null) continue;
                    if ((fieldNode.access & Opcodes.ACC_STATIC) == 0) continue;
                    var value = fieldNode.value;
                    fieldNode.value = null;

                    var firstInsn = clinitMethod.instructions.get(0);
                    clinitMethod.instructions.insertBefore(firstInsn, new LdcInsnNode(value));
                    clinitMethod.instructions.insertBefore(firstInsn, new FieldInsnNode(Opcodes.PUTSTATIC, classNode.name, fieldNode.name, fieldNode.desc));
                    count++;
                }
                Logger.getInstance().info(FieldValueExtractor.class, "Extracted " + count + " fields from " + classNode.name);
            }
        });
    }

    @Override
    public boolean parseConfig(ConfigManager.Configs<String, Object> config) {
        return config.safeGet("extractFieldValues", false);
    }
}
