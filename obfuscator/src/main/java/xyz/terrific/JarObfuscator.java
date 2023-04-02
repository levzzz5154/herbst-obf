package xyz.terrific;

import org.apache.bcel.generic.ClassGen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;

public class JarObfuscator {
    private final File jarfile;

    private static final List<ClassGen> classes = new ArrayList<>();
    private static final List<JarEntry> entries = new ArrayList<>();


    public JarObfuscator(File file) {
        this.jarfile = file;
    }

    public void obfuscate() {

    }


    public static List<ClassGen> getClasses() {
        return classes;
    }

    public static List<JarEntry> getEntries() {
        return entries;
    }
}
