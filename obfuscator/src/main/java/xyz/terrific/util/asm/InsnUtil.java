package xyz.terrific.util.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import xyz.terrific.util.RandomUtil;

import java.util.ArrayList;

public class InsnUtil {
    public static InsnList makeRealJump(final ClassNode classNode, final LabelNode labelNode) {
        final InsnList list = new InsnList();
        switch(RandomUtil.random.nextInt(3)) {
            case 0 -> {
                final FieldNode zeroField = new FieldNode(Opcodes.ACC_STATIC, RandomUtil.randomString(), "I", null, 0);
                classNode.fields.add(zeroField);

                list.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, zeroField.name, zeroField.desc));
                list.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
            }
            case 1 -> {
                final FieldNode nonZeroField = new FieldNode(Opcodes.ACC_STATIC, RandomUtil.randomString(), "I", null, RandomUtil.random.nextInt(1, Integer.MAX_VALUE));
                classNode.fields.add(nonZeroField);

                list.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, nonZeroField.name, nonZeroField.desc));
                list.add(new JumpInsnNode(Opcodes.IFNE, labelNode));
            }
            case 2 -> {
                final FieldNode field3 = new FieldNode(Opcodes.ACC_STATIC, RandomUtil.randomString(), "I", null, RandomUtil.random.nextInt());
                final FieldNode field4 = new FieldNode(Opcodes.ACC_STATIC, RandomUtil.randomString(), "I", null, RandomUtil.random.nextInt());
                classNode.fields.add(field3);
                classNode.fields.add(field4);
                list.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, field3.name, field3.desc));
                list.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, field4.name, field4.desc));

                int opcode = field3.value == field4.value ? Opcodes.IF_ICMPEQ : Opcodes.IF_ICMPNE;
                list.add(new JumpInsnNode(opcode, labelNode));
            }
        }
        return list;
    }
    public static InsnList makeFakeJump(final ClassNode classNode, final ArrayList<LabelNode> labels) {
        final InsnList list = new InsnList();
        switch (RandomUtil.random.nextInt(3)) {
            case 0 -> {
                final FieldNode fieldVal1 = new FieldNode(Opcodes.ACC_STATIC, RandomUtil.randomString(), "I", null, RandomUtil.random.nextInt(1, Integer.MAX_VALUE));
                classNode.fields.add(fieldVal1);
                list.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, fieldVal1.name, fieldVal1.desc));
                list.add(new JumpInsnNode(Opcodes.IFEQ, labels.get(RandomUtil.random.nextInt(labels.size()))));
            }
            case 1 -> {
                final FieldNode fieldVal0 = new FieldNode(Opcodes.ACC_STATIC, RandomUtil.randomString(), "I", null, 0);
                classNode.fields.add(fieldVal0);
                list.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, fieldVal0.name, fieldVal0.desc));
                list.add(new JumpInsnNode(Opcodes.IFNE, labels.get(RandomUtil.random.nextInt(labels.size()))));
            }
            case 2 -> {
                final FieldNode field3 = new FieldNode(Opcodes.ACC_STATIC, RandomUtil.randomString(), "I", null, RandomUtil.random.nextInt());
                final FieldNode field4 = new FieldNode(Opcodes.ACC_STATIC, RandomUtil.randomString(), "I", null, RandomUtil.random.nextInt());
                classNode.fields.add(field3);
                classNode.fields.add(field4);
                list.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, field3.name, field3.desc));
                list.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, field4.name, field4.desc));

                int opcode = field3.value == field4.value ? Opcodes.IF_ICMPNE : Opcodes.IF_ICMPEQ;
                list.add(new JumpInsnNode(opcode, labels.get(RandomUtil.random.nextInt(labels.size()))));
            }
        }
        return list;
    }
}
