package cn.hjf.coverage;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;

public class InstrumentClassVisitor extends ClassVisitor {
    public InstrumentClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM7, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
       final MethodVisitor methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions);

        System.out.println("------" + name + "--------");

        if ("<init>".equals(name) || "<clinit>".equals(name)) {
            return methodVisitor;
        }

        int probeArrayPositionInLocalVariable = (Opcodes.ACC_STATIC & access) == 0 ? 1 : 0;
        for (final Type t : Type.getArgumentTypes(descriptor)) {
            probeArrayPositionInLocalVariable += t.getSize();
        }
        int finalProbeArrayPositionInLocalVariable = probeArrayPositionInLocalVariable;
        System.out.println("finalProbeArrayPositionInLocalVariable : " + finalProbeArrayPositionInLocalVariable);

        return new MethodNode(Opcodes.ASM7, access, name, descriptor, signature, exceptions) {
            @Override
            public void visitEnd() {
                int lineCount = 0;
                for (AbstractInsnNode insnNode : instructions.toArray()) {
                    if (insnNode instanceof LineNumberNode) {
                        lineCount++;
                    }
                }

                System.out.println("lineCount : " + lineCount);

                MethodInstrumenter methodInstrumenter = new MethodInstrumenter(Opcodes.ASM7, methodVisitor);
                methodInstrumenter.setProbeCount(lineCount);
                methodInstrumenter.setProbePosition(finalProbeArrayPositionInLocalVariable);
                accept(methodInstrumenter);
            }
        };
    }
}
