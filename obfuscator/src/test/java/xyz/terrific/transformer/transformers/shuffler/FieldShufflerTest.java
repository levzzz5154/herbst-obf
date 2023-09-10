package xyz.terrific.transformer.transformers.shuffler;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.util.RandomUtil;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class FieldShufflerTest {
    final FieldShuffler shuffler = new FieldShuffler();
    @Test
    void parseConfig() {
        final var config = new ConfigManager.Configs<String, Object>(new HashMap<>());
        config.put("fields", true);
        assertTrue(shuffler.parseConfig(config));
        config.put("fields", false);
        assertFalse(shuffler.parseConfig(config));
    }
    @Test
    void shuffleFields() {
        final var fields = new ArrayList<FieldNode>();
        for (int i = 0; i < RandomUtil.random.nextInt(5, 20); i++) {
            fields.add(new FieldNode(0, RandomUtil.randomString(), "", "", RandomUtil.random.nextInt()));
        }
        final var ogFields = fields.clone();
        FieldShuffler.shuffleFields(fields);
        assertNotEquals(ogFields, fields);
    }
}