package xyz.terrific;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import xyz.terrific.modifiers.ModifierManager;
import xyz.terrific.util.FileUtils;
import xyz.terrific.util.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

public class ClassObfuscator {
    private final File classfile;

    public ClassObfuscator(File file) {
        this.classfile = file;
    }

    public void obfuscate() {
        ClassParser parser = new ClassParser(classfile.getName());
        try {
            JavaClass jc = parser.parse();
            ClassGen gen = new ClassGen(jc);

            ModifierManager.getModifiers().forEach(modifier -> {
                try {
                    Logger.getInstance().info(ClassObfuscator.class, "Running " + modifier.getSimpleName());
                    modifier.getConstructor(ClassGen.class, Boolean.class)
                            .newInstance(gen, false)
                            .transform();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    Logger.getInstance().error("Failed to call constructor of modifier '" + modifier.getSimpleName() + "' - " + e.getMessage());
                }
            });

            File originalfile = new File(classfile.getName().replace(".class", "_bak.class"));
            String originalContent = FileUtils.readFile(classfile);
            if (originalContent == null) {
                Logger.getInstance().warning("Failed to read contents of original class file");
            }

            PrintWriter writer = new PrintWriter(originalfile);
            writer.write(originalContent == null ? "<null>" : originalContent);
            writer.flush();
            writer.close();

            gen.getJavaClass().dump(classfile);
        } catch (IOException e) {
            Logger.getInstance().error("Failed to parse class file: '" + classfile.getName() + "' - " + e.getMessage());
        }
    }
}
