package cn.hjf.cc;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;

public class ClassInstrumenter extends ClassNode {

    private ClassAnalyzer mClassAnalyzer;

    public ClassInstrumenter() {
    }

    public ClassInstrumenter(int api) {
        super(api);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

        MethodInstrumenter methodInstrumenter = new MethodInstrumenter(this.api, access, name, descriptor, signature, exceptions);
        methodInstrumenter.setMethodAnalyzer(mClassAnalyzer.getMethodAnalyzerMap().get(name + descriptor + signature));
        methodInstrumenter.setMethodVisitor(mv);
        methodInstrumenter.setClassNode(this);

        return methodInstrumenter;
    }

    /**
     * ***************************************************************************************************************
     * <p>
     * ***************************************************************************************************************
     */

    public void setClassAnalyzer(ClassAnalyzer classAnalyzer) {
        mClassAnalyzer = classAnalyzer;
    }
}
