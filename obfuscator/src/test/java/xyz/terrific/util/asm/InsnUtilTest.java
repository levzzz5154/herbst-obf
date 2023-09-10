package xyz.terrific.util.asm;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.tree.LabelNode;

class InsnUtilTest {

    @Test
    void parseDescToLocals() {
        var desc = "ZBCSIJFDLjava/lang/String;[[Z[[Ljava/lang/String;";

        var locals = InsnUtil.parseDescToLocals(desc.toCharArray(), new LabelNode(), new LabelNode());
        locals.stream().map(l -> l.desc).forEach(System.out::println);
    }

    @Test
    void getVarInsn() {
    }

    @Test
    void getReturnInsn() {
    }

    @Test
    void getLdcDesc() {
    }

    @Test
    void getOperatorInsnDesc() {
    }
}