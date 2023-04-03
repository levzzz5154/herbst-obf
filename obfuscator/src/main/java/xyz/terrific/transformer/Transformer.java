package xyz.terrific.transformer;

import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.ClassNode;
import xyz.terrific.obfuscator.JarObfuscator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Transformer {
    protected Map<String, ClassNode> classMap;
    protected List<ClassNode> classes;

    public Transformer() {
        this.classMap = JarObfuscator.getClasses();
        this.classes = new ArrayList<>(classMap.values());
    }

    public abstract void transform();


    protected Collection<? extends ClassNode> getImplementing(ClassNode owner) {
        return classes.stream()
                .filter(classNode -> classNode.interfaces.contains(owner.name))
                .collect(Collectors.toList());
    }

    protected Collection<? extends ClassNode> getExtending(ClassNode owner) {
        List<ClassNode> extending = new ArrayList<>();

        classes.stream()
                .filter(classNode -> classNode.superName.equals(owner.name))
                .forEach(classNode -> {
                    extending.add(classNode);
                    extending.addAll(getExtending(classNode));
                });

        return extending;
    }

    protected void applyRemap(Map<String, String> remap) {
        SimpleRemapper remapper = new SimpleRemapper(remap);

        classes.forEach(clazz -> {
            ClassNode copy = new ClassNode();
            ClassRemapper adapter = new ClassRemapper(copy, remapper);

            clazz.accept(adapter);
            classMap.remove(clazz.name);
            classMap.put(clazz.name, copy);
        });
    }
}
