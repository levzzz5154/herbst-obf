package xyz.terrific;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import xyz.terrific.modifiers.ModifierManager;
import xyz.terrific.util.Logger;

import java.io.File;
import java.io.IOException;

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

            ModifierManager.runModifiers(gen);

            File obfuscatedFile = new File(classfile.getName().replace(".class", ".obf.class"));
            Logger.getInstance().info("Saving obfuscated class file to '" + obfuscatedFile.getName() + "'");
            gen.getJavaClass().dump(obfuscatedFile);
        } catch (IOException e) {
            Logger.getInstance().error((Object) "Failed to parse (or failed to save) class file: '%s' - %s", classfile.getName(), e.getMessage());
        }
    }
}
