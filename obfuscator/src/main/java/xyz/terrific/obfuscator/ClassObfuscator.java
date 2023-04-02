package xyz.terrific.obfuscator;

import java.io.File;

public class ClassObfuscator implements IObfuscator {
    private final File classfile;

    public ClassObfuscator(File file) {
        this.classfile = file;
    }

    public void obfuscate() {
    }
}
