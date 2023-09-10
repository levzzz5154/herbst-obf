package xyz.terrific.transformer.transformers.flow;

import org.junit.jupiter.api.Test;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.transformer.transformers.extractor.MethodExtractor;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class MethodExtractorTest {
    final MethodExtractor extractor = new MethodExtractor();
    @Test
    void parseConfig() {
        final var config = new ConfigManager.Configs<String, Object>(new HashMap<>());
        assertFalse(extractor.parseConfig(config));
        config.put("extractMethods", true);
        assertTrue(extractor.parseConfig(config));
        config.put("extractMethods", false);
        assertFalse(extractor.parseConfig(config));
    }
}