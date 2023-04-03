package xyz.terrific.obfuscator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import xyz.terrific.Main;
import xyz.terrific.transformer.TransformerManager;
import xyz.terrific.util.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class JarObfuscator implements IObfuscator {
    private JarFile jarfile;
    private String output;

    private static final Map<String, ClassNode> classes = new HashMap<>();
    private static final Map<String, byte[]> files = new HashMap<>();
    private static Manifest manifest;


    public JarObfuscator(File file) {
        try {
            this.jarfile = new JarFile(file);
            this.output = jarfile.getName().replace(".jar", ".obf.jar");
        } catch (IOException e) {
            Logger.getInstance().error("Failed to create instance of JarFile from file: '%s' - %s", file.getName(), e.getMessage());
        }
    }


    public void init() {
        Enumeration<JarEntry> entries = jarfile.entries();

        try {
            manifest = jarfile.getManifest();
        } catch (IOException e) {
            Logger.getInstance().error((Object) "Failed to get Manifest (continue normal execution)");
        }


        if (TransformerManager.getShouldLog()) {
            Logger.getInstance().info((Object) "Reading jar file '%s'", jarfile.getName());
        }

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();

            byte[] bytes = null;
            try {
                InputStream istream = jarfile.getInputStream(entry);
                ByteArrayOutputStream ostream = new ByteArrayOutputStream();
                byte[] buf = new byte[256];
                int r;
                while ((r = istream.read(buf)) != -1) {
                    ostream.write(buf, 0, r);
                }
                bytes = ostream.toByteArray();
            } catch (IOException e) {
                Logger.getInstance().error((Object) "Failed to read jar entry '%s' - %s", entry.getName(), e.getMessage());
                continue;
            }

            if (entry.getName().endsWith(".class")) {
                ClassNode classNode = new ClassNode();
                new ClassReader(bytes).accept(classNode, ClassReader.EXPAND_FRAMES);
                classes.put(classNode.name, classNode);
            } else {
                files.put(entry.getName(), bytes);
            }
        }
    }

    public void obfuscate() {
        if (TransformerManager.getShouldLog()) {
            Logger.getInstance().info((Object) "Successfully read jar file '%s'", jarfile.getName());
            Logger.getInstance().info((Object) "Running transformers on jar file '%s'", jarfile.getName());
        }

        TransformerManager.runTransformers();

        if (TransformerManager.getShouldLog()) {
            Logger.getInstance().info((Object) "Finished Running transformers on jar file '%s'", jarfile.getName());
        }


        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        if (manifest != null) {
            if (TransformerManager.getShouldLog()) {
                Logger.getInstance().info((Object) "Writing manifest of jar file '%s' to output stream", jarfile.getName());
            }

            try {
                manifest.write(ostream);
                files.put("META-INF/MANIFEST.MF", ostream.toByteArray());
            } catch (IOException e) {
                Logger.getInstance().error((Object) "Failed to write manifest to output stream - %s", e.getMessage());
            }
        }


        if (TransformerManager.getShouldLog()) {
            Logger.getInstance().info((Object) "Creating output file 's'", output);
        }

        Path outputPath = Paths.get(output);
        try {
            Files.deleteIfExists(outputPath);
        } catch (IOException e) {
            Logger.getInstance().error((Object) "Failed to delete output file (if it exists) - %s", e.getMessage());
        }


        if (TransformerManager.getShouldLog()) {
            Logger.getInstance().info((Object) "Starting to write output file 's'", output);
        }
        try {
            JarOutputStream outJar = new JarOutputStream(Files.newOutputStream(outputPath, StandardOpenOption.CREATE, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE));

            if (TransformerManager.getShouldLog()) {
                Logger.getInstance().info((Object) "Writing classes");
            }

            classes.values().forEach(classNode -> {
                try {
                    if (TransformerManager.getShouldLog()) {
                        Logger.getInstance().info((Object) "Writing class '%s'", classNode.name);
                    }

                    outJar.putNextEntry(new JarEntry(classNode.name + ".class"));

                    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                    writer.newUTF8(Main.getName()); // "Signature"
                    classNode.accept(writer);
                    outJar.write(writer.toByteArray());

                    outJar.closeEntry();
                } catch (IOException e) {
                    Logger.getInstance().error((Object) "Failed to write classnode '%s' to output stream - %s", classNode.name, e.getMessage());
                }
            });

            if (TransformerManager.getShouldLog()) {
                Logger.getInstance().info((Object) "Writing files");
            }
            files.forEach((name, file) -> {
                try {
                    if (TransformerManager.getShouldLog()) {
                        Logger.getInstance().info((Object) "Writing file '%s'", name);
                    }

                    outJar.putNextEntry(new JarEntry(name));
                    outJar.write(file);
                    outJar.closeEntry();
                } catch (IOException e) {
                    Logger.getInstance().error((Object) "Failed to write file '%s' - %s", name, e.getMessage());
                }
            });


            outJar.close();
            if (TransformerManager.getShouldLog()) {
                Logger.getInstance().info((Object) "Finished writing obfuscated jar");
            }
        } catch (IOException e) {
            Logger.getInstance().error("Failed to create new output stream for output file '%s' - %s", outputPath.getFileName(), e.getMessage());
        }
    }


    public static Map<String, ClassNode> getClasses() {
        return classes;
    }

    public static Map<String, byte[]> getFiles() {
        return files;
    }
}
