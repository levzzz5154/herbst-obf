package xyz.terrific.transformer.transformers.flow;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.transformer.Transformer;
import xyz.terrific.transformer.annotation.Group;
import xyz.terrific.util.RandomUtil;

import java.util.ArrayList;

@Group(name = "flow")
public class MethodExtractor extends Transformer {
    @Override
    public void transform() {
        classes.stream().filter(c -> !isExcluded(c.name)).forEach(classNode -> {
            var methods = classNode.methods.toArray(new MethodNode[0]);
            for (MethodNode method : methods) {
                method.instructions.forEach(insnNode -> {
                    extractMethodCalls(classNode, method, insnNode);
                });
            }
        });
    }
    public static void extractMethodCalls(ClassNode classNode, MethodNode methodNode, AbstractInsnNode insnNode) {
        if (!(insnNode instanceof MethodInsnNode methodInsn)) return;
        //if (methodInsn.getOpcode() != Opcodes.INVOKEVIRTUAL && methodInsn.getOpcode() != Opcodes.INVOKESTATIC && methodInsn.getOpcode() != Opcodes.INVOKESPECIAL) return;

        final var isStatic = methodInsn.getOpcode() == Opcodes.INVOKESTATIC;
        final var extractedMethodDesc = isStatic ? methodInsn.desc : "(L" + methodInsn.owner + ";" + methodInsn.desc.substring(1);
        final var exMethod = new MethodNode(Opcodes.ACC_STATIC, RandomUtil.randomString(), extractedMethodDesc, null, null);

        final var splitDesc = exMethod.desc.split("\\)");
        final var returnInsn = getReturnInsn(splitDesc[1]);

        final var startLabel = new LabelNode(new Label());
        final var endLabel = new LabelNode(new Label());

        exMethod.instructions.add(startLabel);
        exMethod.localVariables.addAll(parseDescToLocals(splitDesc[0].substring(1).toCharArray(), startLabel, endLabel, 0));

        for (LocalVariableNode localVariable : exMethod.localVariables) {
            exMethod.instructions.add(getVarInsn(localVariable.desc, localVariable.index));
        }
        exMethod.instructions.add(new MethodInsnNode(methodInsn.getOpcode(), methodInsn.owner, methodInsn.name, methodInsn.desc));
        exMethod.instructions.add(returnInsn);
        exMethod.instructions.add(endLabel);
        classNode.methods.add(exMethod);
        methodNode.instructions.insert(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC, classNode.name, exMethod.name, exMethod.desc));
        methodNode.instructions.remove(insnNode);
    }

    public static ArrayList<LocalVariableNode> parseDescToLocals(char[] charArray, LabelNode startLabel, LabelNode endLabel, int startIndex) {
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

    @Override
    public boolean parseConfig(ConfigManager.Configs<String, Object> config) {
        return config.safeGet("extractMethods", false);
    }
}
enum ParsingType {
    NOTHING,
    OBJECT,
    ARRAY
};