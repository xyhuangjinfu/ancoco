package cn.hjf.coverage;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodInstrumenter extends MethodVisitor {

    private int mProbeCount;
    private int mProbePosition;

    public MethodInstrumenter(int api) {
        super(api);
    }

    public MethodInstrumenter(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
    }

    public void setProbeCount(int probeCount) {
        mProbeCount = probeCount;
    }

    public void setProbePosition(int probePosition) {
        mProbePosition = probePosition;
    }

    @Override
    public void visitCode() {
        mv.visitIntInsn(Opcodes.SIPUSH, mProbeCount);
        mv.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_BOOLEAN);
        mv.visitVarInsn(Opcodes.ASTORE, mProbePosition);
    }


    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        mv.visitMaxs(maxStack, maxLocals + 1);
    }
}
