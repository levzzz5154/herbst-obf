package xyz.terrific.obfuscator;

import xyz.terrific.util.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarObfuscator implements IObfuscator {
    private JarFile jarfile;

    /*                          v idk what to put here */
    private static final List<Object> classes = new ArrayList<>();
    private static final List<JarEntry> entries = new ArrayList<>();


    public JarObfuscator(File file) {
        try {
            this.jarfile = new JarFile(file);
        } catch (IOException e) {
            Logger.getInstance().error("Failed to create instance of JarFile from file: '%s' - %s", file.getName(), e.getMessage());
        }
    }

    public void obfuscate() {
    }


    public static List<Object> getClasses() {
        return classes;
    }

    public static List<JarEntry> getEntries() {
        return entries;
    }
}
