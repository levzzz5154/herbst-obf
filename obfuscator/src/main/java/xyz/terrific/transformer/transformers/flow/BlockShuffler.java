package xyz.terrific.transformer.transformers.flow;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
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
                        final ArrayList<ArrayList<AbstractInsnNode>> insnChunks = new ArrayList<>();
                        int chunkSize = RandomUtil.random.nextInt(blockSizeMin, blockSizeMax + 1);

                        // split the method into chunks of instructions
                        for (AbstractInsnNode abstractInsnNode : methodNode.instructions) {
                            if (insnChunks.isEmpty() || insnChunks.get(insnChunks.size() - 1).size() >= chunkSize) {
                                final ArrayList<AbstractInsnNode> chunk = new ArrayList<>();
                                chunk.add(abstractInsnNode);
                                insnChunks.add(chunk);
                                chunkSize = RandomUtil.random.nextInt(blockSizeMin, blockSizeMax + 1);
                                continue;
                            }
                            insnChunks.get(insnChunks.size() - 1).add(abstractInsnNode);
                        }
                        // add labels to the start of every chunk so that they can be jumped to
                        for (ArrayList<AbstractInsnNode> chunk : insnChunks) {
                            if (chunk.isEmpty()) continue;
                            if (!(chunk.get(0) instanceof LabelNode)) {
                                chunk.add(0, new LabelNode(new Label()));
                            }
                        }
                        // add jumps to the next chunk
                        for (int i = 0; i < insnChunks.size(); i++) {
                            final ArrayList<AbstractInsnNode> chunk = insnChunks.get(i);
                            if (chunk.isEmpty()) continue;
                            if ((i + 1) >= insnChunks.size()) continue;
                            final ArrayList<AbstractInsnNode> nextChunk = insnChunks.get(i+1);

                            addInsnListToList(chunk, makeRandomRealJump(classNode, (LabelNode) nextChunk.get(0)));
                        }

                        methodNode.instructions.clear();
                        // add a jump to the first chunk
                        methodNode.instructions.add(makeRandomRealJump(classNode, (LabelNode) insnChunks.get(0).get(0)));
                        // shuffle the chunks, but keep the position of the last one
                        final ArrayList<AbstractInsnNode> lastChunk = insnChunks.get(insnChunks.size() - 1);
                        insnChunks.remove(insnChunks.size() - 1);
                        Collections.shuffle(insnChunks);
                        insnChunks.add(lastChunk);
                        Logger.getInstance().info(BlockShuffler.class.getSimpleName(), "Shuffled " + insnChunks.size() + " blocks " + classNode.name + "." + methodNode.name);

                        for (ArrayList<AbstractInsnNode> chunk : insnChunks) {
                            for (AbstractInsnNode abstractInsnNode : chunk) {
                                methodNode.instructions.add(abstractInsnNode);
                            }
                        }
                    });
                });
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
