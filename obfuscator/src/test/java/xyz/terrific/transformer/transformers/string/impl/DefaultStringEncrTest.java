package xyz.terrific.transformer.transformers.string.impl;

import org.junit.jupiter.api.Test;
import xyz.terrific.util.RandomUtil;

import static org.junit.jupiter.api.Assertions.*;

class DefaultStringEncrTest {

    @Test
    void encryptString() {
        var string = "test string";
        var key = RandomUtil.random.nextInt();
        var encrypted = DefaultStringEncr.encryptString(string, key);
        var encrypted2 = DefaultStringEncr.encryptString(string, key);
        var decrypted = DefaultStringEncr.encryptString(encrypted, key);

        assertNotEquals(string, DefaultStringEncr.encryptString(string, key));
        assertEquals(encrypted, encrypted2);
        assertEquals(decrypted, string);
        var key2 = RandomUtil.random.nextInt();
        assertNotEquals(encrypted, DefaultStringEncr.encryptString(string, key2));
    }
}