package cn.hjf.coverage;

import org.objectweb.asm.ClassVisitor;

public class ClassInstrumenter extends ClassVisitor {
    public ClassInstrumenter(int api) {
        super(api);
    }

    public ClassInstrumenter(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }
}
