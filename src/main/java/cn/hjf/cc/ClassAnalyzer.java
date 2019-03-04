package cn.hjf.cc;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;
import java.util.Map;

public class ClassAnalyzer extends ClassNode {

    private Map<String, MethodAnalyzer> mMethodAnalyzerMap = new HashMap<>();

    public ClassAnalyzer() {
    }

    public ClassAnalyzer(int api) {
        super(api);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

        MethodAnalyzer methodAnalyzer = new MethodAnalyzer(this.api, access, name, descriptor, signature, exceptions);
        methodAnalyzer.setOwner(this);
        methodAnalyzer.setMethodVisitor(mv);

        mMethodAnalyzerMap.put(name + descriptor + signature, methodAnalyzer);

        return methodAnalyzer;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }

    /**
     * ***************************************************************************************************************
     * <p>
     * ***************************************************************************************************************
     */

    public Map<String, MethodAnalyzer> getMethodAnalyzerMap() {
        return mMethodAnalyzerMap;
    }
}
