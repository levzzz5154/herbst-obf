package xyz.terrific.transformer.transformers.string;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.transformer.Transformer;
import xyz.terrific.transformer.annotation.Group;
import xyz.terrific.util.ClassUtil;
import xyz.terrific.util.RandomUtil;

import java.util.concurrent.atomic.AtomicInteger;

import static org.objectweb.asm.Opcodes.*;

@Group(name = "string")
public class StringEncryptor extends Transformer {
    boolean convertToCharArray = false;

    @Override
    public void transform() {
        classes.stream()
                .filter(classNode -> !isExcluded(classNode.name))
                .forEach(classNode -> {
                    var decrMethod = convertToCharArray ? makeCharArrayDecryptMethod() : makeDecryptMethod();
                    AtomicInteger count = new AtomicInteger();
                    ClassUtil.findClinit(classNode).ifPresent(clinitMethod -> {
                        // extract fields
                        for (FieldNode fieldNode : classNode.fields) {
                            if (!(fieldNode.value instanceof String value)) continue;
                            fieldNode.value = null;

                            var firstInsn = clinitMethod.instructions.get(0);
                            clinitMethod.instructions.insertBefore(firstInsn, new LdcInsnNode(value));
                            clinitMethod.instructions.insertBefore(firstInsn, new FieldInsnNode(PUTSTATIC, classNode.name, fieldNode.name, fieldNode.desc));
                        }
                    });

                    classNode.methods.forEach(methodNode -> {
                        // encrypt LDC instructions in methods
                        methodNode.instructions.forEach(insnNode -> {
                            int encryptionKey = RandomUtil.random.nextInt();
                            if (insnNode instanceof LdcInsnNode ldcInsn && ldcInsn.cst instanceof String value) {
                                if (convertToCharArray) {
                                    charArrayEncryption(classNode, decrMethod, methodNode, insnNode, encryptionKey, value);
                                } else {
                                    stringEncryption(classNode, decrMethod, methodNode, insnNode, encryptionKey, value);
                                }

                                count.getAndIncrement();
                            }
                        });

                    });
                    classNode.methods.add(decrMethod);
                    if (count.get() > 0)
                        System.out.println("encrypted strings: " + count.get() + " at " + classNode.name);
                });
    }

    private MethodNode makeDecryptMethod() {
        var name = RandomUtil.randomString();
        var decrMethod = new MethodNode(Opcodes.ACC_STATIC,
                name,
                "(Ljava/lang/String;I)Ljava/lang/String;",
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
        var labelL = new LabelNode();

        decrMethod.localVariables.add(new LocalVariableNode(RandomUtil.randomString(), "Ljava/lang/String;", null, labelA, labelL, 0));
        decrMethod.localVariables.add(new LocalVariableNode(RandomUtil.randomString(), "I", null, labelA, labelL, 1));
        decrMethod.localVariables.add(new LocalVariableNode(RandomUtil.randomString(), "I", null, labelA, labelL, 2));
        decrMethod.localVariables.add(new LocalVariableNode(RandomUtil.randomString(), "I", null, labelB, labelL, 3));
        decrMethod.localVariables.add(new LocalVariableNode(RandomUtil.randomString(), "I", null, labelC, labelL, 4));
        decrMethod.localVariables.add(new LocalVariableNode(RandomUtil.randomString(), "Ljava/lang/Object;", null, labelD, labelL, 5));
        decrMethod.localVariables.add(new LocalVariableNode(RandomUtil.randomString(), "Ljava/lang/Object;", null, labelE, labelL, 6));
        decrMethod.localVariables.add(new LocalVariableNode(RandomUtil.randomString(), "I", null, labelF, labelL, 7));
        decrMethod.localVariables.add(new LocalVariableNode(RandomUtil.randomString(), "I", null, labelH, labelL, 8));

        insnList.add(labelA);
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I"));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "length", "()I"));
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
        insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "toCharArray", "()[C", false));
        insnList.add(new VarInsnNode(Opcodes.ASTORE, 5));

        insnList.add(labelE);
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 5));
        insnList.add(new InsnNode(Opcodes.ARRAYLENGTH));
        insnList.add(new IntInsnNode(NEWARRAY, Opcodes.T_CHAR));
        insnList.add(new VarInsnNode(Opcodes.ASTORE, 6));

        insnList.add(labelF);
        insnList.add(new InsnNode(Opcodes.ICONST_0));
        insnList.add(new VarInsnNode(Opcodes.ISTORE, 7));

        insnList.add(labelG);
        insnList.add(new VarInsnNode(Opcodes.ILOAD, 7));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 5));
        insnList.add(new InsnNode(Opcodes.ARRAYLENGTH));
        insnList.add(new JumpInsnNode(Opcodes.IF_ICMPGE, labelK));

        insnList.add(labelH);
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 5));
        insnList.add(new VarInsnNode(Opcodes.ILOAD, 7));
        insnList.add(new InsnNode(CALOAD));
        insnList.add(new VarInsnNode(Opcodes.ISTORE, 8));

        insnList.add(labelI);
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 6));
        insnList.add(new VarInsnNode(Opcodes.ILOAD, 7));
        insnList.add(new VarInsnNode(Opcodes.ILOAD, 8));
        insnList.add(new VarInsnNode(Opcodes.ILOAD, 3));
        insnList.add(new InsnNode(Opcodes.IXOR));
        insnList.add(new VarInsnNode(Opcodes.ILOAD, 4));
        insnList.add(new InsnNode(Opcodes.IXOR));
        insnList.add(new InsnNode(I2C));
        insnList.add(new InsnNode(CASTORE));

        insnList.add(labelJ);
        insnList.add(new IincInsnNode(7, 1));
        insnList.add(new JumpInsnNode(GOTO, labelG));

        insnList.add(labelK);
        insnList.add(new TypeInsnNode(Opcodes.NEW, "java/lang/String"));
        insnList.add(new InsnNode(Opcodes.DUP));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 6));
        insnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/String", "<init>", "([C)V", false));
        insnList.add(new InsnNode(ARETURN));
        insnList.add(labelL);

        decrMethod.instructions = insnList;
        return decrMethod;
    }

    public static String encryptString(String value, int key) {
        int newKey = (value.length() * value.length()) ^ key;

        char k1 = (char) (newKey & 0xFFFF);
        char k2 = (char) ((newKey >> 16) & 0xFFFF);

        char[] chars = value.toCharArray();
        char[] output = new char[chars.length];

        for (int j = 0; j < chars.length; j++) {
            char i = chars[j];
            output[j] = (char) (i ^ k1 ^ k2);
        }
        return new String(output);
    }

    @Override
    public boolean parseConfig(ConfigManager.Configs<String, Object> config) {
        convertToCharArray = config.safeGet("convertToCharArray", false);
        return config.safeGet("enabled", false);
    }

    private static void charArrayEncryption(ClassNode classNode, MethodNode decrMethod, MethodNode methodNode, AbstractInsnNode insnNode, int encryptionKey, String value) {
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

    private MethodNode makeCharArrayDecryptMethod() {
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

    private static void stringEncryption(ClassNode classNode, MethodNode decrMethod, MethodNode methodNode, AbstractInsnNode insnNode, int encryptionKey, String value) {
        var encrypted = encryptString(value, encryptionKey);

        System.out.println("Old: " + value + " Encrypted: " + encrypted);
        if (RandomUtil.random.nextBoolean()) {
            ((LdcInsnNode) insnNode).cst = encrypted;
            methodNode.instructions.insert(insnNode, new MethodInsnNode(INVOKESTATIC, classNode.name, decrMethod.name, decrMethod.desc, false));
            methodNode.instructions.insert(insnNode, new LdcInsnNode(encryptionKey));
        }
        else {
            final FieldNode encryptedString = new FieldNode(Opcodes.ACC_STATIC, RandomUtil.randomString(), "Ljava/lang/String;", null, encrypted);
            classNode.fields.add(encryptedString);
            methodNode.instructions.insert(insnNode, new MethodInsnNode(INVOKESTATIC, classNode.name, decrMethod.name, decrMethod.desc, false));
            methodNode.instructions.insert(insnNode, new LdcInsnNode(encryptionKey));
            methodNode.instructions.insert(insnNode, new FieldInsnNode(Opcodes.GETSTATIC, classNode.name, encryptedString.name, encryptedString.desc));
            methodNode.instructions.remove(insnNode);
        }
    }
}
