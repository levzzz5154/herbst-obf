package xyz.terrific.modifiers.modifiers;

import org.apache.bcel.classfile.ConstantFieldref;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import xyz.terrific.JarObfuscator;
import xyz.terrific.modifiers.Modifier;
import xyz.terrific.modifiers.ModifierManager;
import xyz.terrific.util.Logger;
import xyz.terrific.util.RandomUtil;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class FieldRenamer extends Modifier {
    public FieldRenamer(ClassGen cg, Boolean isJarFile) {
        super(cg, isJarFile);
    }


    @Override
    public void transform() {
        Arrays.stream(this.classgen.getFields()).forEach(field -> {
            ConstantPoolGen constantPoolGen = classgen.getConstantPool();

            String originalFieldName = field.getName();
            int index = field.getNameIndex();

            String newName = RandomUtil.generateRandomString(ModifierManager.getRandomLength());
            int newIndex = constantPoolGen.addUtf8(newName);
            constantPoolGen.setConstant(index, constantPoolGen.getConstant(newIndex));

            if (ModifierManager.getShouldLog()) {
                Logger.getInstance().info(FieldRenamer.class, "Renamed '%s' (%d) to '%s' (%d)", originalFieldName, index, newName, newIndex);
            }

            if (isJarFile) {
                AtomicInteger count = new AtomicInteger();
                JarObfuscator.getClasses().forEach(clazz -> {
                    int fieldRef = clazz.getConstantPool().lookupFieldref(classgen.getClassName(), originalFieldName, field.getSignature());
                    if (fieldRef <= -1) {
                        return;
                    }

                    ConstantNameAndType nameAndType = (ConstantNameAndType) clazz.getConstantPool().getConstant(
                            ((ConstantFieldref) clazz.getConstantPool().getConstant(fieldRef)).getNameAndTypeIndex());
                    int nameIndex = nameAndType.getNameIndex();
                    if (nameIndex <= -1) {
                        return;
                    }

                    ConstantUtf8 utf8 = (ConstantUtf8) constantPoolGen.getConstant(nameIndex);
                    if (utf8 != null) {
                        count.getAndIncrement();
                        utf8.setBytes(newName);
                    }
                });

                if (ModifierManager.getShouldLog()) {
                    Logger.getInstance().info(FieldRenamer.class, "Renamed %s references to '%s' to '%s'", String.valueOf(count.get()), originalFieldName, newName);
                }
            }
        });
    }
}
