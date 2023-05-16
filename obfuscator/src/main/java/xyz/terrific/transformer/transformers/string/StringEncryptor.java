package xyz.terrific.transformer.transformers.string;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.transformer.Transformer;
import xyz.terrific.transformer.annotation.Group;
import xyz.terrific.util.RandomUtil;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import static org.objectweb.asm.Opcodes.*;

@Group(name = "string")
public class StringEncryptor extends Transformer {
    @Override
    public void transform() {
        classes.stream()
                .filter(classNode -> !isExcluded(classNode.name))
                .forEach(classNode -> {
                    var decrMethod = makeDecryptMethod();
                    classNode.methods.forEach(methodNode -> {
                        AtomicInteger count = new AtomicInteger();
                        methodNode.instructions.forEach(insnNode -> {
                            int encryptionKey = RandomUtil.random.nextInt();
                            if (insnNode instanceof LdcInsnNode ldcInsn && ldcInsn.cst instanceof String value) {
                                var encrypted = encrypt(value, encryptionKey);

                                System.out.println("Old: " + value + " Encrypted: " + encrypted);
                                ((LdcInsnNode) insnNode).cst = encrypted;
                                methodNode.instructions.insert(insnNode, new MethodInsnNode(INVOKESTATIC, classNode.name, decrMethod.name, decrMethod.desc, false));
                                methodNode.instructions.insert(insnNode, new LdcInsnNode(encryptionKey));
                                count.getAndIncrement();
                            }
                        });
                        System.out.println("encrypted strings: " + count.get());
                    });
                    classNode.methods.add(decrMethod);
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

        decrMethod.localVariables.add(new LocalVariableNode("value", "Ljava/lang/String;", null, labelA, labelL, 0));
        decrMethod.localVariables.add(new LocalVariableNode("key", "I", null, labelA, labelL, 1));
        decrMethod.localVariables.add(new LocalVariableNode("newKey", "I", null, labelA, labelL, 2));
        decrMethod.localVariables.add(new LocalVariableNode("k1", "I", null, labelB, labelL, 3));
        decrMethod.localVariables.add(new LocalVariableNode("k2", "I", null, labelC, labelL, 4));
        decrMethod.localVariables.add(new LocalVariableNode("chars", "Ljava/lang/Object;", null, labelD, labelL, 5));
        decrMethod.localVariables.add(new LocalVariableNode("output", "Ljava/lang/Object;", null, labelE, labelL, 6));
        decrMethod.localVariables.add(new LocalVariableNode("j", "I", null, labelF, labelL, 7));
        decrMethod.localVariables.add(new LocalVariableNode("i", "I", null, labelH, labelL, 8));

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

    public static String encrypt(String value, int key) {
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
        return config.safeGet("enabled", false);
    }
}
