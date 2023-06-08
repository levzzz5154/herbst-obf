package xyz.terrific.transformer.transformers.flow;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.transformer.Transformer;
import xyz.terrific.transformer.annotation.Group;
import xyz.terrific.util.Logger;
import xyz.terrific.util.RandomUtil;

@Group(name = "flow")
public class NumberEncryptor extends Transformer {
    @Override
    public void transform() {
        classes.stream().filter(classNode -> !isExcluded(classNode.name)).forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                final var insnArray = methodNode.instructions.toArray();
                for (AbstractInsnNode insnNode : insnArray) {
                    final var insnList = obfNumberConstant(insnNode);
                    if (insnList.size() > 0) {
                        Logger.getInstance().info(NumberEncryptor.class, "replaced " + insnList.size() + " number constants at " + classNode.name + "." + methodNode.name);
                        methodNode.instructions.insert(insnNode, insnList);
                        methodNode.instructions.remove(insnNode);
                    }
                }
            });
        });
    }

    private static InsnList obfNumberConstant(final AbstractInsnNode insnNode) {
        final var insnList = new InsnList();
        switch (insnNode) {
            case LdcInsnNode ldcInsn && ldcInsn.cst instanceof Integer -> {
                final var key = RandomUtil.random.nextInt();
                var encryptedValue = ((int) ldcInsn.cst) ^ key;

                if (RandomUtil.random.nextBoolean()) {
                    encryptedValue = Integer.reverse((int)ldcInsn.cst);
                    insnList.add(new LdcInsnNode(encryptedValue));
                    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Integer", "reverse", "(I)I", false));
                }
                else insnList.add(new LdcInsnNode(encryptedValue));

                insnList.add(new LdcInsnNode(key));
                insnList.add(new InsnNode(Opcodes.IXOR));
            }
            case LdcInsnNode ldcInsn && ldcInsn.cst instanceof Long -> {
                final var key = RandomUtil.random.nextLong();
                final var encryptedValue = ((long) ldcInsn.cst) ^ key;
                insnList.add(new LdcInsnNode(key));
                insnList.add(new LdcInsnNode(encryptedValue));
                insnList.add(new InsnNode(Opcodes.LXOR));
            }
            case LdcInsnNode ldcInsn && ldcInsn.cst instanceof Float -> {
                final var key = RandomUtil.random.nextInt();
                final var value = (float)ldcInsn.cst;
                insnList.add(new LdcInsnNode(Float.floatToRawIntBits(value) ^ key));
                insnList.add(new LdcInsnNode(key));
                insnList.add(new InsnNode(Opcodes.IXOR));
                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Float", "intBitsToFloat", "(I)F", false));
            }
            case LdcInsnNode ldcInsn && ldcInsn.cst instanceof Double -> {
                final var key = RandomUtil.random.nextLong();
                final var value = (double)ldcInsn.cst;

                insnList.add(new LdcInsnNode(Double.doubleToRawLongBits(value) ^ key));
                insnList.add(new LdcInsnNode(key));
                insnList.add(new InsnNode(Opcodes.LXOR));
                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Double", "longBitsToDouble", "(J)D", false));
            }
            case IntInsnNode intInsn && (intInsn.getOpcode() == Opcodes.SIPUSH || intInsn.getOpcode() == Opcodes.BIPUSH) -> {
                final var key = RandomUtil.random.nextInt();
                final var value = intInsn.operand;

                insnList.add(new LdcInsnNode(key));
                insnList.add(new LdcInsnNode(value ^ key));
                insnList.add(new InsnNode(Opcodes.IXOR));
            }
            default -> {}
        }
        return insnList;
    }

    @Override
    public boolean parseConfig(ConfigManager.Configs<String, Object> config) {
        return config.safeGet("encryptNumbers", false);
    }
}
