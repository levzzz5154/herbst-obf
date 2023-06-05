package xyz.terrific.transformer.transformers.flow;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.*;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.transformer.Transformer;
import xyz.terrific.transformer.annotation.Group;
import xyz.terrific.util.Logger;
import xyz.terrific.util.RandomUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static xyz.terrific.util.asm.InsnUtil.makeRandomRealJump;

@Group(name = "flow")
public class BlockShuffler extends Transformer {

    public int blockSizeMin = -1;
    public int blockSizeMax = -1;

    @Override
    public void transform() {
        classes.stream()
                .filter(classNode -> !isExcluded(classNode.name))
                .forEach(classNode -> {
                    classNode.methods.forEach(methodNode -> {
                        obfuscateMethod(classNode, methodNode);
                    });
                });
    }

    private void obfuscateMethod(final ClassNode classNode, final MethodNode methodNode) {
        var insnChunks = splitInsnList(methodNode.instructions, blockSizeMin, blockSizeMax);

        if (insnChunks.isEmpty()) return;

        addLabelsToChunks(insnChunks);
        linkChunks(classNode, insnChunks);
        methodNode.instructions.clear();
        // add a jump to the first chunk
        methodNode.instructions.add(makeRandomRealJump(classNode, (LabelNode) insnChunks.get(0).get(0)));
        // shuffle the chunks, but keep the position of the last one
        shuffleChunks(insnChunks);

        Logger.getInstance().info(BlockShuffler.class, "Shuffled " + insnChunks.size() + " blocks " + classNode.name + "." + methodNode.name);

        for (ArrayList<AbstractInsnNode> chunk : insnChunks) {
            for (AbstractInsnNode abstractInsnNode : chunk) {
                methodNode.instructions.add(abstractInsnNode);
            }
        }
    }
    public static ArrayList<ArrayList<AbstractInsnNode>> splitInsnList(final InsnList instructions, final int minSize, final int maxSize) {
        final ArrayList<ArrayList<AbstractInsnNode>> insnChunks = new ArrayList<>();
        var chunkSize = RandomUtil.random.nextInt(minSize, maxSize + 1);
        if (instructions.size() == 0)
            return insnChunks;

        for (AbstractInsnNode abstractInsnNode : instructions) {
            if (insnChunks.isEmpty() || insnChunks.get(insnChunks.size() - 1).size() >= chunkSize) {
                final ArrayList<AbstractInsnNode> chunk = new ArrayList<>();
                chunk.add(abstractInsnNode);
                insnChunks.add(chunk);
                chunkSize = RandomUtil.random.nextInt(minSize, maxSize + 1);
                continue;
            }
            insnChunks.get(insnChunks.size() - 1).add(abstractInsnNode);
        }
        return insnChunks;
    }
    public static void addLabelsToChunks(final ArrayList<ArrayList<AbstractInsnNode>> insnChunks) {
        for (ArrayList<AbstractInsnNode> chunk : insnChunks) {
            if (chunk.isEmpty()) continue;
            if (!(chunk.get(0) instanceof LabelNode)) {
                chunk.add(0, new LabelNode(new Label()));
            }
        }
    }
    public static void linkChunks(final ClassNode classNode, final ArrayList<ArrayList<AbstractInsnNode>> insnChunks) {
        for (int i = 0; i < insnChunks.size(); i++) {
            final ArrayList<AbstractInsnNode> chunk = insnChunks.get(i);
            if (chunk.isEmpty()) continue;
            if ((i + 1) >= insnChunks.size()) continue;
            final ArrayList<AbstractInsnNode> nextChunk = insnChunks.get(i+1);

            addInsnListToList(chunk, makeRandomRealJump(classNode, (LabelNode) nextChunk.get(0)));
        }
    }
    public static void shuffleChunks(final ArrayList<ArrayList<AbstractInsnNode>> insnChunks) {
        final ArrayList<AbstractInsnNode> lastChunk = insnChunks.get(insnChunks.size() - 1);
        insnChunks.remove(insnChunks.size() - 1);
        Collections.shuffle(insnChunks);
        insnChunks.add(lastChunk);
    }
    private static void addInsnListToList(final List<AbstractInsnNode> list, final InsnList insnList) {
        for (AbstractInsnNode abstractInsnNode : insnList) {
            list.add(abstractInsnNode);
        }
    }
    @Override
    public boolean parseConfig(ConfigManager.Configs<String, Object> config) {
        blockSizeMin = config.safeGet("blockSizeMin", 4);
        blockSizeMax = config.safeGet("blockSizeMax", 6);
        return config.safeGet("shuffleBlocks", false);
    }
}
