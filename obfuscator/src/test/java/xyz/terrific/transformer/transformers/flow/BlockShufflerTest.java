package xyz.terrific.transformer.transformers.flow;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.util.RandomUtil;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class BlockShufflerTest {
    final BlockShuffler shuffler = new BlockShuffler();
    @org.junit.jupiter.api.Test
    void splitInsnList() {
        final var insnList = new InsnList();
        final var size = RandomUtil.random.nextInt(5, 20);
        final var allInsns = RandomUtil.random.nextInt(1, 10) * size;
        for (int i = 0; i < allInsns; i++) {
            insnList.add(new LdcInsnNode(RandomUtil.random.nextInt()));
        }
        final var chunks = BlockShuffler.splitInsnList(insnList, size, size);
        for (ArrayList<AbstractInsnNode> chunk : chunks) {
            assertEquals(chunk.size(), size);
        }
    }
    @org.junit.jupiter.api.Test
    void addLabelsToChunks() {
        final var chunk = new ArrayList<AbstractInsnNode>();
        for (int i = 0; i < RandomUtil.random.nextInt(5, 20); i++) {
            chunk.add(new LdcInsnNode(RandomUtil.random.nextInt()));
        }
        final var chunks = new ArrayList<ArrayList<AbstractInsnNode>>();
        for (int i = 0; i < RandomUtil.random.nextInt(1, 10); i++) {
            chunks.add(chunk);
        }
        BlockShuffler.addLabelsToChunks(chunks);
        for (ArrayList<AbstractInsnNode> insnChunk : chunks) {
            assertTrue(insnChunk.get(0) instanceof LabelNode);
            assertFalse(insnChunk.get(1) instanceof LabelNode);
        }
    }
    @org.junit.jupiter.api.Test
    void linkChunks() {

    }
    @org.junit.jupiter.api.Test
    void shuffleChunks() {
        final var chunk = new ArrayList<AbstractInsnNode>();
        chunk.add(new LabelNode());
        for (int i = 0; i < RandomUtil.random.nextInt(5, 20); i++) {
            chunk.add(new LdcInsnNode(RandomUtil.random.nextInt()));
        }
        final var chunks = new ArrayList<ArrayList<AbstractInsnNode>>();
        for (int i = 0; i < RandomUtil.random.nextInt(1, 10); i++) {
            chunks.add(chunk);
        }
        final var firstChunk = chunks.get(0);
        BlockShuffler.shuffleChunks(chunks);
        assertEquals(firstChunk, chunks.get(0));
    }
    @org.junit.jupiter.api.Test
    void parseConfig() {
        final var config = new ConfigManager.Configs<>(new HashMap<String, Object>());
        final var blockSizeMin = RandomUtil.random.nextInt(1, 10);
        final var blockSizeMax = RandomUtil.random.nextInt(blockSizeMin, 30);

        config.put("blockSizeMin", blockSizeMin);
        config.put("blockSizeMax", blockSizeMax);
        config.put("shuffleBlocks", true);

        assertTrue(shuffler.parseConfig(config));
        assertEquals(blockSizeMin, shuffler.blockSizeMin);
        assertEquals(blockSizeMax, shuffler.blockSizeMax);

        config.put("shuffleBlocks", false);
        assertFalse(shuffler.parseConfig(config));
    }
}