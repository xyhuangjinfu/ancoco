package cn.hjf.cc;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;

public class ClassInstrumenter extends ClassNode {

    private ClassAnalyzer mClassAnalyzer;
    private int mProbeIndex;

    public ClassInstrumenter() {
    }

    public ClassInstrumenter(int api) {
        super(api);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

        MethodAnalyzer analyzer = mClassAnalyzer.getMethodAnalyzerMap().get(name + descriptor + signature);

        MethodInstrumenter methodInstrumenter = new MethodInstrumenter(this.api, access, name, descriptor, signature, exceptions);
        methodInstrumenter.setMethodAnalyzer(analyzer);
        methodInstrumenter.setMethodVisitor(mv);
        methodInstrumenter.setClassNode(this);

        int totalLineCount = 0;
        for (MethodAnalyzer methodAnalyzer : mClassAnalyzer.getMethodAnalyzerMap().values()) {
            totalLineCount += methodAnalyzer.getLineCount();
        }

        methodInstrumenter.setTotalLineCount(totalLineCount);

        methodInstrumenter.setProbeIndex(mProbeIndex);
        mProbeIndex += analyzer.getLineCount();

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
