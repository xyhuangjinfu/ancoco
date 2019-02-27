package cn.hjf.coverage;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;

public class MethodAnalyser extends MethodNode {

	private MethodVisitor mMethodVisitor;
	private int mProbeArrayPositionInLocalVariable;
	private int mLineCount;

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

		mProbeArrayPositionInLocalVariable = (Opcodes.ACC_STATIC & access) == 0 ? 1 : 0;
		for (final Type t : Type.getArgumentTypes(descriptor)) {
			mProbeArrayPositionInLocalVariable += t.getSize();
		}
	}

	public void setMethodVisitor(MethodVisitor methodVisitor) {
		mMethodVisitor = methodVisitor;
	}

	@Override
	public void visitEnd() {
		for (AbstractInsnNode insnNode : instructions.toArray()) {
			if (insnNode instanceof LineNumberNode) {
				mLineCount++;
			}
		}

		MethodInstrumenter methodInstrumenter = new MethodInstrumenter(Opcodes.ASM7, mMethodVisitor);
		methodInstrumenter.setProbeCount(mLineCount);
		methodInstrumenter.setProbePosition(mProbeArrayPositionInLocalVariable);
		accept(methodInstrumenter);
	}
}
