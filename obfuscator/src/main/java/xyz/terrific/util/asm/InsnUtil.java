package xyz.terrific.util.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import xyz.terrific.util.RandomUtil;

import java.util.ArrayList;

public class InsnUtil {
    public static InsnList makeRealJump(final ClassNode classNode, final LabelNode labelNode) {
        final InsnList list = new InsnList();
        switch(RandomUtil.random.nextInt(4)) {
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
            case 3 -> {
                final FieldNode field3 = new FieldNode(Opcodes.ACC_STATIC, RandomUtil.randomString(), "J", null, RandomUtil.random.nextLong());
                final FieldNode field4 = new FieldNode(Opcodes.ACC_STATIC, RandomUtil.randomString(), "J", null, RandomUtil.random.nextLong());
                classNode.fields.add(field3);
                classNode.fields.add(field4);
                list.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, field3.name, field3.desc));
                list.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, field4.name, field4.desc));

                list.add(new InsnNode(Opcodes.LCMP));
                int opcode = field3.value == field4.value ? Opcodes.IFEQ : Opcodes.IFNE;
                list.add(new JumpInsnNode(opcode, labelNode));
            }
        }
        return list;
    }
    public static InsnList makeFakeJump(final ClassNode classNode, final ArrayList<LabelNode> labels) {
        final InsnList list = new InsnList();
        switch (RandomUtil.random.nextInt(4)) {
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
            case 3 -> {
                final FieldNode field3 = new FieldNode(Opcodes.ACC_STATIC, RandomUtil.randomString(), "J", null, RandomUtil.random.nextLong());
                final FieldNode field4 = new FieldNode(Opcodes.ACC_STATIC, RandomUtil.randomString(), "J", null, RandomUtil.random.nextLong());
                classNode.fields.add(field3);
                classNode.fields.add(field4);
                list.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, field3.name, field3.desc));
                list.add(new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, field4.name, field4.desc));

                list.add(new InsnNode(Opcodes.LCMP));
                int opcode = field3.value == field4.value ? Opcodes.IFNE : Opcodes.IFEQ;
                list.add(new JumpInsnNode(opcode, labels.get(RandomUtil.random.nextInt(labels.size()))));
            }
        }
        return list;
    }
    public static ArrayList<LocalVariableNode> parseDescToLocals(char[] charArray, LabelNode startLabel, LabelNode endLabel) {
        int startIndex = 0;
        StringBuilder localVarDesc = new StringBuilder();
        final var localVars = new ArrayList<LocalVariableNode>();
        var nowParsing = ParsingType.NOTHING;

        for (char c : charArray) {
            switch (nowParsing) {
                case NOTHING -> {
                    if (c == 'L') {
                        nowParsing = ParsingType.OBJECT;
                        localVarDesc.append(c);
                    } else if (c == '[') {
                        nowParsing = ParsingType.ARRAY;
                        localVarDesc.append(c);
                    } else {
                        localVars.add(new LocalVariableNode(RandomUtil.randomString(), String.valueOf(c), null, startLabel, endLabel, startIndex));
                        startIndex++;
                    }
                }
                case OBJECT -> {
                    localVarDesc.append(c);
                    if (c == ';') {
                        nowParsing = ParsingType.NOTHING;
                        localVars.add(new LocalVariableNode(RandomUtil.randomString(), localVarDesc.toString(), null, startLabel, endLabel, startIndex));
                        localVarDesc = new StringBuilder();
                        startIndex++;
                    }
                }
                case ARRAY -> {
                    localVarDesc.append(c);
                    if (c == 'L') {
                        nowParsing = ParsingType.OBJECT;
                    } else if (c != '[') {
                        nowParsing = ParsingType.NOTHING;
                        localVars.add(new LocalVariableNode(RandomUtil.randomString(), localVarDesc.toString(), null, startLabel, endLabel, startIndex));
                        localVarDesc = new StringBuilder();
                        startIndex++;
                    }
                }
            }
        }
        return localVars;
    }
    enum ParsingType {
        NOTHING,
        OBJECT,
        ARRAY
    }
    public static VarInsnNode getVarInsn(String desc, int varIndex) {
        return switch (desc) {
            case "Z", "B", "C", "S", "I" -> new VarInsnNode(Opcodes.ILOAD, varIndex);
            case "J" -> new VarInsnNode(Opcodes.LLOAD, varIndex);
            case "F" -> new VarInsnNode(Opcodes.FLOAD, varIndex);
            case "D" -> new VarInsnNode(Opcodes.DLOAD, varIndex);
            default -> new VarInsnNode(Opcodes.ALOAD, varIndex);
        };
    }
    public static InsnNode getReturnInsn(String desc) {
        return switch (desc) {
            case "Z", "B", "C", "S", "I" -> new InsnNode(Opcodes.IRETURN);
            case "J" -> new InsnNode(Opcodes.LRETURN);
            case "F" -> new InsnNode(Opcodes.FRETURN);
            case "D" -> new InsnNode(Opcodes.DRETURN);
            case "V" -> new InsnNode(Opcodes.RETURN);
            default -> new InsnNode(Opcodes.ARETURN);
        };
    }
    public static String getLdcDesc(LdcInsnNode ldcInsn) {
        return switch (ldcInsn.cst) {
            case String ignored -> "Ljava/lang/String;";
            case Integer ignored -> "I";
            case Float ignored -> "F";
            case Long ignored -> "J";
            case Double ignored -> "D";
            case Class<?> ignored -> "Ljava/lang/Class;";
            case Object ignored -> "I"; // TODO: figure out if this is divine intellect? or just a dirty hack?
        };
    }
    public static String getOperatorInsnDesc(InsnNode operatorInsn) {
        var desc = "";
        var op = operatorInsn.getOpcode();
        if (op >= Opcodes.IADD && op <= Opcodes.DREM) {
            switch (op % 4) {
                case 0 -> desc = "I";
                case 1 -> desc = "J";
                case 2 -> desc = "F";
                case 3 -> desc = "D";
            }
        } else if (op >= Opcodes.ISHL && op <= Opcodes.LXOR) {
            switch (op % 2) {
                case 0 -> desc = "I";
                case 1 -> desc = "J";
            }
        }
        return desc;
    }
}
