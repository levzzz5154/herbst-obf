package xyz.terrific.transformer.transformers.flow;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.transformer.Transformer;
import xyz.terrific.transformer.annotation.Group;
import xyz.terrific.util.ClassUtil;
import xyz.terrific.util.Logger;

import java.util.concurrent.atomic.AtomicInteger;

@Group(name = "flow")
public class FieldValueExtractor extends Transformer {
    @Override
    public void transform() {
        classes.stream().filter(classNode -> !isExcluded(classNode.name)).forEach(classNode -> {
            ClassUtil.findClinit(classNode).ifPresent(clinitMethod -> {
                AtomicInteger count = new AtomicInteger();
                classNode.fields.stream()
                        .filter(fieldNode -> fieldNode.value != null)
                        .filter(fieldNode -> (fieldNode.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC)
                        .forEach(fieldNode -> {
                            var value = fieldNode.value;
                            fieldNode.value = null;

                            var firstInsn = clinitMethod.instructions.get(0);
                            clinitMethod.instructions.insertBefore(firstInsn, new LdcInsnNode(value));
                            clinitMethod.instructions.insertBefore(firstInsn, new FieldInsnNode(Opcodes.PUTSTATIC, classNode.name, fieldNode.name, fieldNode.desc));
                            count.getAndIncrement();
                        });
                Logger.getInstance().info(FieldValueExtractor.class, "Extracted " + count + " fields from " + classNode.name);
            });

        });
    }

    @Override
    public boolean parseConfig(ConfigManager.Configs<String, Object> config) {
        return config.safeGet("extractFieldValues", false);
    }
}
