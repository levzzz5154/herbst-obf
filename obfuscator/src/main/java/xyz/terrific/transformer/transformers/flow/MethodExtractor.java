package xyz.terrific.transformer.transformers.flow;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.transformer.Transformer;
import xyz.terrific.transformer.annotation.Group;
import xyz.terrific.util.RandomUtil;

import java.util.ArrayList;

import static xyz.terrific.util.asm.InsnUtil.*;

@Group(name = "flow")
public class MethodExtractor extends Transformer {
    @Override
    public void transform() {
        classes.stream().filter(c -> !isExcluded(c.name)).forEach(classNode -> {
            var methods = classNode.methods.toArray(new MethodNode[0]);
            for (MethodNode method : methods) {
                method.instructions.forEach(insnNode -> {
                    switch (insnNode) {
                        case MethodInsnNode methodInsnNode -> {
                            extractMethodInsn(classNode, method, insnNode);
                        }
                        case LdcInsnNode ldcInsnNode -> {
                            extractLdcInsn(classNode, method, ldcInsnNode);
                        }
                        case InsnNode operatorInsn
                                && ((operatorInsn.getOpcode() >= Opcodes.IADD && operatorInsn.getOpcode() <= Opcodes.DREM)
                                || (operatorInsn.getOpcode() >= Opcodes.ISHL && operatorInsn.getOpcode() <= Opcodes.LXOR)) -> {
                            extractOperatorInsn(classNode, method, operatorInsn);
                        }
                        default -> {
                        }
                    }
                });
            }
        });
    }

    public static void extractOperatorInsn(ClassNode classNode, MethodNode methodNode, InsnNode operatorInsn) {
        final var opInsnDesc = getOperatorInsnDesc(operatorInsn);
        if (opInsnDesc.isEmpty()) return;
        final var exMethodDesc = "(" + opInsnDesc.repeat(2) + ")" + opInsnDesc;
        final var exMethod = new MethodNode(Opcodes.ACC_STATIC, RandomUtil.randomString(), exMethodDesc, null, null);

        final var splitDesc = exMethod.desc.split("\\)");
        final var returnInsn = getReturnInsn(splitDesc[1]);

        final var startLabel = new LabelNode(new Label());
        final var endLabel = new LabelNode(new Label());
        final var secondIndex = (opInsnDesc.equals("J") || opInsnDesc.equals("D")) ? 2 : 1;

        /*exMethod.localVariables.add(new LocalVariableNode(RandomUtil.randomString(), leReturnDesc, null, startLabel, endLabel, 0));
        exMethod.localVariables.add(new LocalVariableNode(RandomUtil.randomString(), leReturnDesc, null, startLabel, endLabel, 1));*/

        exMethod.instructions.add(startLabel);
        exMethod.instructions.add(getVarInsn(opInsnDesc, 0));
        exMethod.instructions.add(getVarInsn(opInsnDesc, secondIndex));
        exMethod.instructions.add(operatorInsn.clone(null));
        exMethod.instructions.add(returnInsn);
        exMethod.instructions.add(endLabel);
        classNode.methods.add(exMethod);
        methodNode.instructions.insert(operatorInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, classNode.name, exMethod.name, exMethod.desc));
        methodNode.instructions.remove(operatorInsn);
    }
    public static void extractLdcInsn(ClassNode classNode, MethodNode methodNode, LdcInsnNode ldcInsn) {
        final var leReturnDesc = getLdcDesc(ldcInsn);
        final var exMethod = new MethodNode(Opcodes.ACC_STATIC, RandomUtil.randomString(), "()" + leReturnDesc, null, null);

        final var splitDesc = exMethod.desc.split("\\)");
        final var returnInsn = getReturnInsn(splitDesc[1]);

        final var startLabel = new LabelNode(new Label());
        final var endLabel = new LabelNode(new Label());

        exMethod.instructions.add(startLabel);
        exMethod.instructions.add(ldcInsn.clone(null));
        exMethod.instructions.add(returnInsn);
        exMethod.instructions.add(endLabel);
        classNode.methods.add(exMethod);
        methodNode.instructions.insert(ldcInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, classNode.name, exMethod.name, exMethod.desc));
        methodNode.instructions.remove(ldcInsn);
    }
    public static void extractMethodInsn(ClassNode classNode, MethodNode methodNode, AbstractInsnNode insnNode) {
        if (!(insnNode instanceof MethodInsnNode methodInsn)) return;

        final var isStatic = methodInsn.getOpcode() == Opcodes.INVOKESTATIC;
        final var extractedMethodDesc = isStatic ? methodInsn.desc : "(L" + methodInsn.owner + ";" + methodInsn.desc.substring(1);
        final var exMethod = new MethodNode(Opcodes.ACC_STATIC, RandomUtil.randomString(), extractedMethodDesc, null, null);

        final var splitDesc = exMethod.desc.split("\\)");
        final var returnInsn = getReturnInsn(splitDesc[1]);

        final var startLabel = new LabelNode(new Label());
        final var endLabel = new LabelNode(new Label());

        exMethod.instructions.add(startLabel);
        exMethod.localVariables.addAll(parseDescToLocals(splitDesc[0].substring(1).toCharArray(), startLabel, endLabel));

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
    @Override
    public boolean parseConfig(ConfigManager.Configs<String, Object> config) {
        return config.safeGet("extractMethods", false);
    }
}