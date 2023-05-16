package xyz.terrific.transformer.transformers.flow;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.transformer.Transformer;
import xyz.terrific.transformer.annotation.Group;
import xyz.terrific.util.Logger;
import xyz.terrific.util.RandomUtil;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Group(name = "flow")
public class FakeJumpAdder extends Transformer {
    public int fakeJumpChancePercent = 10;

    @Override
    public void transform() {
        classes.stream()
                .filter(classNode -> !isExcluded(classNode.name))
                .forEach(classNode -> {
                    classNode.methods.forEach(methodNode -> {
                        AtomicInteger addedFakeJumps = new AtomicInteger();
                        final ArrayList<LabelNode> labels = new ArrayList<>();
                        methodNode.instructions.forEach(abstractInsnNode -> {
                            if (abstractInsnNode instanceof LabelNode labelNode) {
                                labels.add(labelNode);
                            }
                        });
                        labels.remove(labels.size() - 1); // Remove the last label because it's after the RETURN insn

                        if (!labels.isEmpty()) {
                            methodNode.instructions.forEach(abstractInsnNode -> {
                                if (!(abstractInsnNode instanceof LabelNode) && RandomUtil.random.nextInt(100) < fakeJumpChancePercent) {
                                    addAFakeJump(classNode, methodNode, labels, abstractInsnNode);
                                    addedFakeJumps.getAndIncrement();
                                }
                            });
                        }

                        Logger.getInstance().info(FakeJumpAdder.class.getSimpleName(),"added " + addedFakeJumps + " fake jumps: " + classNode.name + "." + methodNode.name);
                    });
                });
    }

    private void addAFakeJump(ClassNode classNode, MethodNode methodNode, ArrayList<LabelNode> labels, AbstractInsnNode abstractInsnNode) {
        switch (RandomUtil.random.nextInt(3)) {
            case 0:
                final FieldNode fieldVal1 = new FieldNode(Opcodes.ACC_STATIC, RandomUtil.randomString(20), "I", null, RandomUtil.random.nextInt(1, Integer.MAX_VALUE));
                classNode.fields.add(fieldVal1);
                methodNode.instructions.insertBefore(abstractInsnNode, new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, fieldVal1.name, fieldVal1.desc));
                methodNode.instructions
                        .insertBefore(abstractInsnNode, new JumpInsnNode(Opcodes.IFEQ, labels.get(RandomUtil.random.nextInt(labels.size()))));
                break;
            case 1:
                final FieldNode fieldVal0 = new FieldNode(Opcodes.ACC_STATIC, RandomUtil.randomString(20), "I", null, 0);
                classNode.fields.add(fieldVal0);
                methodNode.instructions.insertBefore(abstractInsnNode, new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, fieldVal0.name, fieldVal0.desc));
                methodNode.instructions
                        .insertBefore(abstractInsnNode, new JumpInsnNode(Opcodes.IFNE, labels.get(RandomUtil.random.nextInt(labels.size()))));
                break;
            case 2:
                final FieldNode field3 = new FieldNode(Opcodes.ACC_STATIC, RandomUtil.randomString(20), "I", null, RandomUtil.random.nextInt());
                final FieldNode field4 = new FieldNode(Opcodes.ACC_STATIC, RandomUtil.randomString(20), "I", null, RandomUtil.random.nextInt());
                classNode.fields.add(field3);
                classNode.fields.add(field4);

                methodNode.instructions.insertBefore(abstractInsnNode, new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, field3.name, field3.desc));
                methodNode.instructions.insertBefore(abstractInsnNode, new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, field4.name, field4.desc));
                int opcode;
                if (field3.value == field4.value) opcode = Opcodes.IF_ICMPNE;
                else opcode = Opcodes.IF_ICMPEQ;

                methodNode.instructions.insertBefore(abstractInsnNode, new JumpInsnNode(opcode, labels.get(RandomUtil.random.nextInt(labels.size()))));
                break;
        }
    }

    @Override
    public boolean parseConfig(ConfigManager.Configs<String, Object> config) {
        fakeJumpChancePercent = config.safeGet("fakeJumpChancePercent", 10);

        return config.safeGet("addFakeJumps", false)
                && config.safeGet("fakeJumpChancePercent", 0) > 0;
    }
}
