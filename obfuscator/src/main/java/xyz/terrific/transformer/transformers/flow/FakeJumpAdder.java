package xyz.terrific.transformer.transformers.flow;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.transformer.Transformer;
import xyz.terrific.transformer.annotation.Group;
import xyz.terrific.util.Logger;
import xyz.terrific.util.RandomUtil;
import xyz.terrific.util.asm.InsnUtil;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Group(name = "flow")
public class FakeJumpAdder extends Transformer {
    public int fakeJumpChancePercent = 10;

    @Override
    public void transform() {
        classes.stream()
                .filter(classNode -> !isExcluded(classNode.name))
                .forEach(classNode -> {
                    classNode.methods.stream()
                            .filter(methodNode -> methodNode.instructions.size() > 0)
                            .forEach(methodNode -> {
                        final var fakeJumpCount = new AtomicInteger();
                        final ArrayList<LabelNode> labels = new ArrayList<>();
                        methodNode.instructions.forEach(abstractInsnNode -> {
                            if (abstractInsnNode instanceof LabelNode labelNode) {
                                labels.add(labelNode);
                            }
                        });
                        if (!labels.isEmpty())
                            labels.remove(labels.size() - 1); // Remove the last label because it's after the RETURN insn

                        if (!labels.isEmpty()) {
                            methodNode.instructions.forEach(abstractInsnNode -> {
                                if (!(abstractInsnNode instanceof LabelNode) && RandomUtil.random.nextInt(100) < fakeJumpChancePercent) {
                                    methodNode.instructions.insertBefore(abstractInsnNode, InsnUtil.makeFakeJump(classNode, labels));
                                    fakeJumpCount.getAndIncrement();
                                }
                            });
                        }
                        if (fakeJumpCount.get() > 0)
                            Logger.getInstance().info(FakeJumpAdder.class.getSimpleName(),"added " + fakeJumpCount + " fake jumps: " + classNode.name + "." + methodNode.name);
                    });
                });
    }

    @Override
    public boolean parseConfig(ConfigManager.Configs<String, Object> config) {
        fakeJumpChancePercent = config.safeGet("fakeJumpChancePercent", 10);

        return config.safeGet("addFakeJumps", false)
                && config.safeGet("fakeJumpChancePercent", 0) > 0;
    }
}
