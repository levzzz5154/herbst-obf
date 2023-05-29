package xyz.terrific.transformer.transformers.flow;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.transformer.Transformer;
import xyz.terrific.transformer.annotation.Group;
import xyz.terrific.util.RandomUtil;

@Group(name = "flow")
public class NumberEncryptor extends Transformer {
    @Override
    public void transform() {
        classes.stream().filter(classNode -> !isExcluded(classNode.name)).forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                for (int i = 0; i < 1; i++) {
                    methodNode.instructions.forEach(insnNode -> {
                        obfNumberConstants(methodNode, insnNode);
                    });
                }
            });
        });
    }

    private static void obfNumberConstants(MethodNode methodNode, AbstractInsnNode insnNode) {
        switch (insnNode) {
            case LdcInsnNode ldcInsn && ldcInsn.cst instanceof Integer -> {
                final var key = RandomUtil.random.nextInt();
                final var insnList = new InsnList();

                ldcInsn.cst = ((int) ldcInsn.cst) ^ key;
                if (RandomUtil.random.nextBoolean()) {
                    ldcInsn.cst = Integer.reverse((int)ldcInsn.cst);
                    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "reverse", "(I)I", false));
                }
                insnList.add(new LdcInsnNode(key));
                insnList.add(new InsnNode(Opcodes.IXOR));
                methodNode.instructions.insert(insnNode, insnList);
            }
            case LdcInsnNode ldcInsn && ldcInsn.cst instanceof Long -> {
                final var key = RandomUtil.random.nextLong();
                ldcInsn.cst = ((long) ldcInsn.cst) ^ key;
                methodNode.instructions.insertBefore(insnNode, new LdcInsnNode(key));
                methodNode.instructions.insert(insnNode, new InsnNode(Opcodes.LXOR));
            }
            case LdcInsnNode ldcInsn && ldcInsn.cst instanceof Float -> {
                final var key = RandomUtil.random.nextInt();
                final var value = (float)ldcInsn.cst;

                final var insnList = new InsnList();
                insnList.add(new LdcInsnNode(Float.floatToRawIntBits(value) ^ key));
                insnList.add(new LdcInsnNode(key));
                insnList.add(new InsnNode(Opcodes.IXOR));
                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Float", "intBitsToFloat", "(I)F", false));

                methodNode.instructions.insert(insnNode, insnList);
                methodNode.instructions.remove(insnNode);
            }
            case LdcInsnNode ldcInsn && ldcInsn.cst instanceof Double -> {
                final var key = RandomUtil.random.nextLong();
                final var value = (double)ldcInsn.cst;

                final var insnList = new InsnList();
                insnList.add(new LdcInsnNode(Double.doubleToRawLongBits(value) ^ key));
                insnList.add(new LdcInsnNode(key));
                insnList.add(new InsnNode(Opcodes.LXOR));
                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Double", "longBitsToDouble", "(J)D", false));

                methodNode.instructions.insert(insnNode, insnList);
                methodNode.instructions.remove(insnNode);
            }
            case IntInsnNode intInsn && (intInsn.getOpcode() == Opcodes.SIPUSH || intInsn.getOpcode() == Opcodes.BIPUSH) -> {
                final var key = RandomUtil.random.nextInt();
                final var value = intInsn.operand;

                methodNode.instructions.insertBefore(insnNode, new LdcInsnNode(key));
                methodNode.instructions.insert(insnNode, new InsnNode(Opcodes.IXOR));
                methodNode.instructions.insert(insnNode, new LdcInsnNode(value ^ key));
                methodNode.instructions.remove(insnNode);
            }
            default -> {}
        }
    }

    @Override
    public boolean parseConfig(ConfigManager.Configs<String, Object> config) {
        return config.safeGet("encryptNumbers", false);
    }
}
