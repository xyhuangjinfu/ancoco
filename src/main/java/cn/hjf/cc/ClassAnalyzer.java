package cn.hjf.cc;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;

public class ClassAnalyzer extends ClassNode {

    private List<MethodAnalyzer> mMethodAnalyzerList = new ArrayList<>();

    public ClassAnalyzer() {
    }

    public ClassAnalyzer(int api) {
        super(api);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodAnalyzer methodAnalyzer = new MethodAnalyzer(this.api, access, name, descriptor, signature, exceptions);
        methodAnalyzer.setOwner(this);

        mMethodAnalyzerList.add(methodAnalyzer);

        return methodAnalyzer;
    }

    @Override
    public void visitEnd() {
        for (MethodAnalyzer methodAnalyzer : mMethodAnalyzerList) {
            System.out.println(methodAnalyzer.name + " , " + methodAnalyzer.getTotalLineCount() + " , " + methodAnalyzer.getBranchLineList());
        }

    }
}
