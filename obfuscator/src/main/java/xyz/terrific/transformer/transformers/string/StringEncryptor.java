package xyz.terrific.transformer.transformers.string;

import org.objectweb.asm.tree.*;
import xyz.terrific.config.ConfigManager;
import xyz.terrific.transformer.Transformer;
import xyz.terrific.transformer.annotation.Group;
import xyz.terrific.transformer.transformers.string.impl.CharArrayStringEncr;
import xyz.terrific.transformer.transformers.string.impl.DefaultStringEncr;
import xyz.terrific.util.ClassUtil;

import java.util.concurrent.atomic.AtomicInteger;

import static org.objectweb.asm.Opcodes.*;

@Group(name = "string")
public class StringEncryptor extends Transformer {
    IStringEncr encryptor;

    @Override
    public void transform() {
        classes.stream()
                .filter(classNode -> !isExcluded(classNode.name))
                .forEach(classNode -> {
                    AtomicInteger count = new AtomicInteger();
                    ClassUtil.findClinit(classNode).ifPresent(clinitMethod -> {
                        // extract fields
                        for (FieldNode fieldNode : classNode.fields) {
                            if (!(fieldNode.value instanceof String value)) continue;
                            fieldNode.value = null;

                            var firstInsn = clinitMethod.instructions.get(0);
                            clinitMethod.instructions.insertBefore(firstInsn, new LdcInsnNode(value));
                            clinitMethod.instructions.insertBefore(firstInsn, new FieldInsnNode(PUTSTATIC, classNode.name, fieldNode.name, fieldNode.desc));
                        }
                    });
                    var decrMethod = encryptor.makeDecryptMethod();
                    classNode.methods.forEach(methodNode -> {
                        // encrypt LDC instructions in methods
                        methodNode.instructions.forEach(insnNode -> {
                            if (insnNode instanceof LdcInsnNode ldcInsn && ldcInsn.cst instanceof String value) {
                                encryptor.encrypt(classNode, decrMethod, methodNode, insnNode, value);
                                count.getAndIncrement();
                            }
                        });

                    });
                    classNode.methods.add(decrMethod);
                    if (count.get() > 0)
                        System.out.println("encrypted strings: " + count.get() + " at " + classNode.name);
                });
    }



    @Override
    public boolean parseConfig(ConfigManager.Configs<String, Object> config) {
        if (config.safeGet("convertToCharArray", false)) {
            encryptor = new CharArrayStringEncr();
        } else {
            encryptor = new DefaultStringEncr();
        }
        return config.safeGet("enabled", false);
    }
}
