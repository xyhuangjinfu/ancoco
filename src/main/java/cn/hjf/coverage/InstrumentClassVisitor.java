package cn.hjf.coverage;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM7;

public class InstrumentClassVisitor extends ClassVisitor {
    public InstrumentClassVisitor(ClassVisitor classVisitor) {
        super(ASM7, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        System.out.println("-----------------visitMethod----------" + name);

        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);

        if ("add".equals(name)) {
            return new AddNopAdapter(mv);
        }
        return mv;

//        return new AddNopAdapter(mv);
    }
}
