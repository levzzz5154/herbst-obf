package xyz.terrific.transformer.transformers.flow;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.transformer.Transformer;
import xyz.terrific.transformer.annotation.Group;
import xyz.terrific.util.ClassUtil;
import xyz.terrific.util.Logger;
import xyz.terrific.util.RandomUtil;
import xyz.terrific.util.asm.InsnUtil;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Group(name = "flow")
public class UselessInsns extends Transformer {
    @Override
    public void transform() {
        AtomicInteger amount = new AtomicInteger();
        classes.stream().filter(classNode -> !isExcluded(classNode.name))
            .forEach(classNode -> {
                var leMethods = classNode.methods.stream()
                    .filter(methodNode -> methodNode.instructions.size() > 0)
                    .filter(methodNode -> !methodNode.name.equals("<clinit>"))
                    .toList();
                leMethods.forEach(methodNode -> {
                    methodNode.instructions.forEach(insnNode -> {
                        if (RandomUtil.random.nextInt(1, 101) <= 10) {
                            methodNode.instructions.insertBefore(insnNode, makeFakeInsns(classNode));
                            amount.getAndIncrement();
                        }
                    });
                });
            });
        Logger.getInstance().info("uselessinsns", "added " + amount + " useless insns");
    }

    public InsnList makeFakeInsns(ClassNode classNode) {
        var insnList = new InsnList();

        switch (RandomUtil.random.nextInt(4)) {
            case 0 -> {
                insnList.add(new LdcInsnNode(RandomUtil.random.nextInt()));
                insnList.add(new InsnNode(Opcodes.POP));
            }
            case 1 -> {
                final FieldNode zeroField = new FieldNode(Opcodes.ACC_STATIC, RandomUtil.randomString(), "I", null, RandomUtil.random.nextInt());
                classNode.fields.add(zeroField);

                insnList.add(new LdcInsnNode(RandomUtil.random.nextInt()));
                insnList.add(new FieldInsnNode(Opcodes.PUTSTATIC, classNode.name, zeroField.name, zeroField.desc));
            }
            case 2 -> {
                final MethodNode newMethod = new MethodNode(Opcodes.ACC_STATIC, RandomUtil.randomString(), "()V", null, null);
                newMethod.instructions.add(new InsnNode(Opcodes.RETURN));
                classNode.methods.add(newMethod);
                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, classNode.name, newMethod.name, newMethod.desc));
            }
            case 3 -> {
                final var methodToCall = classNode.methods.get(RandomUtil.random.nextInt(classNode.methods.size()));
                final var opcode = (methodToCall.access & Opcodes.ACC_STATIC) == 0 ? Opcodes.INVOKEVIRTUAL : Opcodes.INVOKESTATIC;
                final var newLabel = new LabelNode();
                insnList.add(InsnUtil.makeRealJump(classNode, newLabel));
                insnList.add(new MethodInsnNode(opcode, classNode.name, methodToCall.name, methodToCall.desc));
                insnList.add(newLabel);
            }
        }

        return insnList;
    }

    @Override
    public boolean parseConfig(ConfigManager.Configs<String, Object> config) {
        return true;
    }
}
