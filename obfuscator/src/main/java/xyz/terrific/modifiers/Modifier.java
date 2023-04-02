package xyz.terrific.modifiers;

import org.apache.bcel.generic.ClassGen;

public abstract class Modifier {
    protected ClassGen classgen;
    protected boolean isJarFile;

    public Modifier(ClassGen cg, Boolean isJarFile) {
        classgen = cg;
        this.isJarFile = isJarFile;
    }

    public abstract void transform();
}
