package xyz.terrific.transformer.transformers.string;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public interface IStringEncr {
    MethodNode makeDecryptMethod();
    void encrypt(ClassNode classNode, MethodNode decrMethod, MethodNode methodNode, AbstractInsnNode insnNode, String value);
}
