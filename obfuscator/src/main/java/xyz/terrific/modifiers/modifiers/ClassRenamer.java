package xyz.terrific.modifiers.modifiers;

import com.sun.xml.internal.bind.v2.bytecode.ClassTailor;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Type;
import xyz.terrific.JarObfuscator;
import xyz.terrific.modifiers.Modifier;
import xyz.terrific.modifiers.ModifierManager;
import xyz.terrific.util.Logger;
import xyz.terrific.util.RandomUtil;

import java.util.Arrays;

@SuppressWarnings({"deprecation"})
public class ClassRenamer extends Modifier {
    public ClassRenamer(ClassGen cg, Boolean isJarFile) {
        super(cg, isJarFile);
    }

    @Override
    public void transform() {
        if (classgen.containsMethod("main", Type.getMethodSignature(Type.VOID, new Type[] { new ArrayType(Type.STRING, 1) })) != null) {
            Logger.getInstance().info(ClassTailor.class, "Skipping class '%s' because it contains the main function", classgen.getClassName()); // TODO: save manifest file in variable and modify classname corresponding to this
            return;
        }

        String classname = classgen.getClassName();
        int nameIndex = classname.lastIndexOf(".");
        String newName;
        if (nameIndex > -1) {
            newName = classname.substring(0, classname.lastIndexOf('.')) + '.' + RandomUtil.generateRandomString(ModifierManager.getRandomLength());
        } else {
            newName = RandomUtil.generateRandomString(ModifierManager.getRandomLength());
        }
        classgen.setClassName(newName);

        String filename = classgen.getFileName();
        int ut = classgen.getConstantPool().lookupUtf8(filename);
        if (ut > -1) {
            ConstantUtf8 c = (ConstantUtf8) classgen.getConstantPool().getConstant(ut);
            c.setBytes(RandomUtil.generateRandomString(ModifierManager.getRandomLength()) + ".java");
            Logger.getInstance().info("Changed source file name from '%s.java' to '%s'", filename, c.getBytes());
        }

        replaceReferences(classgen, classname, newName);
        if (ModifierManager.getShouldLog()) {
            Logger.getInstance().info("Replaced references (descriptors) to '%s' with '%s'", classname, newName);
        }
        
        if (isJarFile) {
            JarObfuscator.getClasses().forEach(gen -> {
                ConstantPoolGen constantPoolGen = gen.getConstantPool();
                replaceReferences(gen, classname, newName);

                int index = constantPoolGen.lookupClass(classname);
                if (index > -1) {
                    ConstantClass cclass = (ConstantClass) constantPoolGen.getConstant(index);
                    ConstantUtf8 utf8 = (ConstantUtf8) constantPoolGen.getConstant(cclass.getNameIndex());
                    utf8.setBytes(newName.replace('.', '/'));

                    if (ModifierManager.getShouldLog()) {
                        Logger.getInstance().info("renamed '%s' to '%s' in class '%s'", classname, newName, gen.getClassName());
                    }
                }
            });
        }
    }

    private void replaceReferences(ClassGen classgen, String classname, String newName) {
        ConstantPoolGen constantPoolGen = classgen.getConstantPool();

        Arrays.stream(constantPoolGen.getConstantPool().getConstantPool()).forEach(constant -> {
            if (!(constant instanceof ConstantUtf8)) return;

            ConstantUtf8 utf8 = (ConstantUtf8) constant;
            String brokenClassName = classname.replace('.', '/');

            if (utf8.getBytes().contains("L" + brokenClassName + ";")) {
                utf8.setBytes(utf8.getBytes().replace("L" + brokenClassName + ";", "L" + newName.replace('.', '/') + ";"));
            }
        });
    }
}
