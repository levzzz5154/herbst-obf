package xyz.terrific;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import xyz.terrific.modifiers.ModifierManager;
import xyz.terrific.util.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class JarObfuscator {
    private JarFile jarfile;

    private static final List<ClassGen> classes = new ArrayList<>();
    private static final List<JarEntry> entries = new ArrayList<>();


    public JarObfuscator(File file) {
        try {
            this.jarfile = new JarFile(file);
        } catch (IOException e) {
            Logger.getInstance().error(String.format("Failed to create instance of JarFile from file: '%s' - %s", file.getName(), e.getMessage()));
        }
    }

    public void obfuscate() {
        try {
            Enumeration<JarEntry> e_entries = jarfile.entries();
            JarOutputStream out = new JarOutputStream(Files.newOutputStream(Paths.get(jarfile.getName().replace(".jar", ".obf.jar"))));
            while (e_entries.hasMoreElements()) {
                JarEntry entry = e_entries.nextElement();
                JarEntry newEntry = new JarEntry(entry.getName());

                if (entry.getName().endsWith(".class")) {
                    ClassParser parser = new ClassParser(jarfile.getInputStream(entry), entry.getName());
                    JavaClass javaclass = parser.parse();
                    Repository.addClass(javaclass);
                    ClassGen gen = new ClassGen(javaclass);

                    entries.add(newEntry);
                    classes.add(gen);
                } else {
                    out.putNextEntry(newEntry);
                    byte[] buf = new byte[1024];
                    InputStream stream = jarfile.getInputStream(entry);

                    int b;
                    while ((b = stream.read(buf)) != -1) {
                        out.write(buf, 0, b);
                    }

                    out.flush();
                    out.closeEntry();
                }
            }


            classes.forEach(gen -> {
                ModifierManager.runModifiers(gen);

                String name = gen.getClassName().replace(".", "/").concat(".class");
                JarEntry entry = new JarEntry(name);
                try {
                    out.putNextEntry(entry);
                    out.write(gen.getJavaClass().getBytes());
                    out.flush();
                    out.closeEntry();

                    if (ModifierManager.getShouldLog()) {
                        Logger.getInstance().info(JarObfuscator.class, String.format("Saved obfuscated entry '%s'", name));
                    }
                } catch (IOException e) {
                    Logger.getInstance().error("Failed to call putNextEntry on 'out' - " + e.getMessage());
                }
            });

            out.close();
        } catch (IOException e) {
            Logger.getInstance().error(String.format("Failed to create jar output stream for file '%s' - %s\n\t\tOr failed to parse JavaClass", jarfile.getName(), e.getMessage()));
        }
    }


    public static List<ClassGen> getClasses() {
        return classes;
    }

    public static List<JarEntry> getEntries() {
        return entries;
    }
}
