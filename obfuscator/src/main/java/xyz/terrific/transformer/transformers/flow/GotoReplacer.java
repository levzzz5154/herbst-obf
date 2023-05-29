package xyz.terrific.transformer.transformers.flow;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.JumpInsnNode;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.transformer.Transformer;
import xyz.terrific.transformer.annotation.Group;
import xyz.terrific.util.Logger;

import java.util.concurrent.atomic.AtomicInteger;

import static xyz.terrific.util.asm.InsnUtil.makeRandomRealJump;

@Group(name = "flow")
public class GotoReplacer extends Transformer {
    @Override
    public void transform() {
        classes.stream().filter(classNode -> !isExcluded(classNode.name)).forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                AtomicInteger count = new AtomicInteger(0);
                methodNode.instructions.forEach(insnNode -> {
                    if (insnNode instanceof JumpInsnNode gotoInsn && insnNode.getOpcode() == Opcodes.GOTO) {

                        methodNode.instructions.insert(insnNode, makeRandomRealJump(classNode, gotoInsn.label));
                        methodNode.instructions.remove(insnNode);
                        count.incrementAndGet();
                    }
                });
                if (count.get() > 0) {
                    Logger.getInstance().info(GotoReplacer.class, "replaced " + count + " GOTOs at " + classNode.name + "." + methodNode.name);
                }
            });
        });
    }

    @Override
    public boolean parseConfig(ConfigManager.Configs<String, Object> config) {
        return config.safeGet("replaceGotos", false);
    }
}
