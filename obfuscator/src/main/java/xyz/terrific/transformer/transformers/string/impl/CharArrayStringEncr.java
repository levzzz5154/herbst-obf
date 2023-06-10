package xyz.terrific.transformer.transformers.string.impl;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import xyz.terrific.transformer.transformers.string.IStringEncr;
import xyz.terrific.util.RandomUtil;

import static org.objectweb.asm.Opcodes.*;

public class CharArrayStringEncr implements IStringEncr {
    @Override
    public MethodNode makeDecryptMethod() {
        var name = RandomUtil.randomString();
        var decrMethod = new MethodNode(Opcodes.ACC_STATIC,
                name,
                "([CI)Ljava/lang/String;",
                null,
                null);

        var insnList = new InsnList();
        var labelA = new LabelNode();
        var labelB = new LabelNode();
        var labelC = new LabelNode();
        var labelD = new LabelNode();
        var labelE = new LabelNode();
        var labelF = new LabelNode();
        var labelG = new LabelNode();
        var labelH = new LabelNode();
        var labelI = new LabelNode();
        var labelJ = new LabelNode();
        var labelK = new LabelNode();

        decrMethod.localVariables.add(new LocalVariableNode("chars", "[C", null, labelA, labelK, 0));
        decrMethod.localVariables.add(new LocalVariableNode("key", "I", null, labelA, labelK, 1));
        decrMethod.localVariables.add(new LocalVariableNode("newKey", "I", null, labelA, labelK, 2));
        decrMethod.localVariables.add(new LocalVariableNode("k1", "I", null, labelB, labelK, 3));
        decrMethod.localVariables.add(new LocalVariableNode("k2", "I", null, labelC, labelK, 4));
        decrMethod.localVariables.add(new LocalVariableNode("output", "Ljava/lang/Object;", null, labelD, labelK, 5));
        decrMethod.localVariables.add(new LocalVariableNode("j", "I", null, labelE, labelK, 6));
        decrMethod.localVariables.add(new LocalVariableNode("i", "I", null, labelG, labelK, 7));

        insnList.add(labelA);
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(new InsnNode(ARRAYLENGTH));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(new InsnNode(ARRAYLENGTH));
        insnList.add(new InsnNode(Opcodes.IMUL));
        insnList.add(new VarInsnNode(Opcodes.ILOAD, 1));
        insnList.add(new InsnNode(Opcodes.IXOR));
        insnList.add(new VarInsnNode(Opcodes.ISTORE, 2));

        insnList.add(labelB);
        insnList.add(new VarInsnNode(Opcodes.ILOAD, 2));
        insnList.add(new LdcInsnNode(65535));
        insnList.add(new InsnNode(Opcodes.IAND));
        insnList.add(new InsnNode(I2C));
        insnList.add(new VarInsnNode(Opcodes.ISTORE, 3));

        insnList.add(labelC);
        insnList.add(new VarInsnNode(Opcodes.ILOAD, 2));
        insnList.add(new IntInsnNode(BIPUSH, 16));
        insnList.add(new InsnNode(ISHR));
        insnList.add(new LdcInsnNode(65535));
        insnList.add(new InsnNode(Opcodes.IAND));
        insnList.add(new InsnNode(I2C));
        insnList.add(new VarInsnNode(Opcodes.ISTORE, 4));

        insnList.add(labelD);
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(new InsnNode(Opcodes.ARRAYLENGTH));
        insnList.add(new IntInsnNode(NEWARRAY, Opcodes.T_CHAR));
        insnList.add(new VarInsnNode(Opcodes.ASTORE, 5));

        insnList.add(labelE);
        insnList.add(new InsnNode(Opcodes.ICONST_0));
        insnList.add(new VarInsnNode(Opcodes.ISTORE, 6));

        insnList.add(labelF);
        insnList.add(new VarInsnNode(Opcodes.ILOAD, 6));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(new InsnNode(Opcodes.ARRAYLENGTH));
        insnList.add(new JumpInsnNode(Opcodes.IF_ICMPGE, labelJ));

        insnList.add(labelG);
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(new VarInsnNode(Opcodes.ILOAD, 6));
        insnList.add(new InsnNode(CALOAD));
        insnList.add(new VarInsnNode(Opcodes.ISTORE, 7));

        insnList.add(labelH);
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 5));
        insnList.add(new VarInsnNode(Opcodes.ILOAD, 6));
        insnList.add(new VarInsnNode(Opcodes.ILOAD, 7));
        insnList.add(new VarInsnNode(Opcodes.ILOAD, 3));
        insnList.add(new InsnNode(Opcodes.IXOR));
        insnList.add(new VarInsnNode(Opcodes.ILOAD, 4));
        insnList.add(new InsnNode(Opcodes.IXOR));
        insnList.add(new InsnNode(I2C));
        insnList.add(new InsnNode(CASTORE));

        insnList.add(labelI);
        insnList.add(new IincInsnNode(6, 1));
        insnList.add(new JumpInsnNode(GOTO, labelF));

        insnList.add(labelJ);
        insnList.add(new TypeInsnNode(Opcodes.NEW, "java/lang/String"));
        insnList.add(new InsnNode(Opcodes.DUP));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 5));
        insnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/String", "<init>", "([C)V", false));
        insnList.add(new InsnNode(ARETURN));
        insnList.add(labelK);

        decrMethod.instructions = insnList;
        return decrMethod;
    }

    @Override
    public void encrypt(ClassNode classNode, MethodNode decrMethod, MethodNode methodNode, AbstractInsnNode insnNode, String value) {
        int encryptionKey = RandomUtil.random.nextInt();
        var encrypted = encryptCharArray(value.toCharArray(), encryptionKey);

        System.out.println("Old: " + value + " Encrypted: " + encrypted);
        methodNode.instructions.insert(insnNode, new MethodInsnNode(INVOKESTATIC, classNode.name, decrMethod.name, decrMethod.desc, false));
        methodNode.instructions.insert(insnNode, new LdcInsnNode(encryptionKey));
        final var insnList = new InsnList();
        final var ldcChars = encrypted.toCharArray();
        insnList.add(new LdcInsnNode(ldcChars.length));
        insnList.add(new IntInsnNode(NEWARRAY, T_CHAR));
        for (int i = 0; i < ldcChars.length; i++) {
            char leChar = ldcChars[i];
            insnList.add(new InsnNode(DUP));
            insnList.add(new LdcInsnNode(i));
            insnList.add(new LdcInsnNode(leChar));
            insnList.add(new InsnNode(CASTORE));
        }
        methodNode.instructions.insert(insnNode, insnList);
        methodNode.instructions.remove(insnNode);
    }

    public static String encryptCharArray(char[] chars, int key) {
        int newKey = (chars.length * chars.length) ^ key;

        char k1 = (char) (newKey & 0xFFFF);
        char k2 = (char) ((newKey >> 16) & 0xFFFF);

        char[] output = new char[chars.length];

        for (int j = 0; j < chars.length; j++) {
            char i = chars[j];
            output[j] = (char) (i ^ k1 ^ k2);
        }
        return new String(output);
    }
}
