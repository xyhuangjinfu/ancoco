package cn.hjf.coverage;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.ASM7;

public class AddNopAdapter extends MethodVisitor {
    public AddNopAdapter(MethodVisitor methodVisitor) {
        super(ASM7, methodVisitor);
    }

//    @Override
//    public void visitInsn(int opcode) {
//        super.visitInsn(opcode);
//
//        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//        mv.visitLdcInsn("------insn executed-----");
//        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
//    }

        @Override
    public void visitLineNumber(int line, Label start) {
        super.visitLineNumber(line, start);

        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("------line executed-----");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
    }

//    @Override
//    public void visitCode() {
//        super.visitCode();
//
//        System.out.println("-----AddNopAdapter visitCode----");
//
//
//        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//        mv.visitLdcInsn("------enter method-----");
//        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
//    }
}
