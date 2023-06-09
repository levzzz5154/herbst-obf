package xyz.terrific.util;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Optional;
import java.util.function.Consumer;

public class ClassUtil {
    public static String getClassName(String name) {
        if (name.contains("/")) {
            return name.substring(name.lastIndexOf("/"));
        }
        return name;
    }
    public static Optional<MethodNode> findClinit(ClassNode classNode) {
        return classNode.methods.stream()
                .filter(methodNode -> methodNode.name.equals("<clinit>")
                        && methodNode.desc.equals("()V")).findFirst();
    }
}
