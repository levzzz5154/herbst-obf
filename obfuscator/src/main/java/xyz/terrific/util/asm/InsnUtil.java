package xyz.terrific.util.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import xyz.terrific.util.RandomUtil;

public class InsnUtil {
    public static InsnList makeRandomRealJump(final ClassNode classNode, final LabelNode labelNode) {
        return switch(RandomUtil.random.nextInt(2)) {
            case 0 -> {
                final FieldNode zeroField = new FieldNode(Opcodes.ACC_STATIC, RandomUtil.randomString(), "I", null, 0);
                classNode.fields.add(zeroField);

                final InsnList list = new InsnList();
                list.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, zeroField.name, zeroField.desc));
                list.add(new JumpInsnNode(Opcodes.IFEQ, labelNode));
                yield list;
            }
            case 1 -> {
                final FieldNode nonZeroField = new FieldNode(Opcodes.ACC_STATIC, RandomUtil.randomString(), "I", null, RandomUtil.random.nextInt(1, Integer.MAX_VALUE));
                classNode.fields.add(nonZeroField);

                final InsnList list = new InsnList();
                list.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, nonZeroField.name, nonZeroField.desc));
                list.add(new JumpInsnNode(Opcodes.IFNE, labelNode));
                yield list;
            }
            default -> {
                final InsnList list = new InsnList();
                list.add(new JumpInsnNode(Opcodes.GOTO, labelNode));
                yield list;
            }
        };
    }
}
