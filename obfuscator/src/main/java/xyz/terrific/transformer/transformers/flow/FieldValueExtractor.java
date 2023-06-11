package xyz.terrific.transformer.transformers.flow;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.transformer.Transformer;
import xyz.terrific.transformer.annotation.Group;
import xyz.terrific.util.ClassUtil;
import xyz.terrific.util.Logger;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Group(name = "flow")
public class FieldValueExtractor extends Transformer {
    @Override
    public void transform() {
        classes.stream().filter(classNode -> !isExcluded(classNode.name)).forEach(classNode -> {
            AtomicReference<MethodNode> clMethod = new AtomicReference<>();
            ClassUtil.findClinit(classNode).ifPresentOrElse(clMethod::set, () -> {
                final var clinitMethod = new MethodNode(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
                clinitMethod.instructions.add(new InsnNode(Opcodes.RETURN));
                classNode.methods.add(clinitMethod);
                clMethod.set(clinitMethod);
            });

            AtomicInteger count = new AtomicInteger();
            classNode.fields.stream()
                    .filter(fieldNode -> fieldNode.value != null)
                    .filter(fieldNode -> (fieldNode.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC)
                    .forEach(fieldNode -> {
                        var value = fieldNode.value;
                        fieldNode.value = null;

                        var firstInsn = clMethod.get().instructions.get(0);
                        clMethod.get().instructions.insertBefore(firstInsn, new LdcInsnNode(value));
                        clMethod.get().instructions.insertBefore(firstInsn, new FieldInsnNode(Opcodes.PUTSTATIC, classNode.name, fieldNode.name, fieldNode.desc));
                        count.getAndIncrement();
                    });
            Logger.getInstance().info(FieldValueExtractor.class, "Extracted " + count + " fields from " + classNode.name);

        });
    }

    @Override
    public boolean parseConfig(ConfigManager.Configs<String, Object> config) {
        return config.safeGet("extractFieldValues", false);
    }
}
