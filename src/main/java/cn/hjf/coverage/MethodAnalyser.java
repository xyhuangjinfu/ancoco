package cn.hjf.coverage;

import org.objectweb.asm.tree.MethodNode;

public class MethodAnalyser extends MethodNode {

    public MethodAnalyser() {
    }

    public MethodAnalyser(int api) {
        super(api);
    }

    public MethodAnalyser(int access, String name, String descriptor, String signature, String[] exceptions) {
        super(access, name, descriptor, signature, exceptions);
    }

    public MethodAnalyser(int api, int access, String name, String descriptor, String signature, String[] exceptions) {
        super(api, access, name, descriptor, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
