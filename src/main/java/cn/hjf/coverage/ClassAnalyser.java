package cn.hjf.coverage;

import cn.hjf.test.InstrumentTest;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Map;

public class ClassAnalyser extends ClassNode {

	private int mTotalMethodLineCount;
	private Map<String, Integer> mMethodLineCountMap = new HashMap<>();

	public ClassAnalyser() {
	}

	public ClassAnalyser(int api) {
		super(api);
	}

	@Override
	public void visitEnd() {
		super.visitEnd();

		calculateTotalMethodLineCount();

		insertProbeForMethod();


		ClassWriter classWriter = new ClassWriter(0);

		accept(new ClassInstrumenter(api, classWriter));

		InstrumentTest.writeClass("/Users/huangjinfu/Downloads/HomeActivity1.class", classWriter.toByteArray());
	}

	private void insertProbeForMethod() {

		for (MethodNode methodNode : methods) {
			int probeArrayPositionInLocalVariable = (Opcodes.ACC_STATIC & methodNode.access) == 0 ? 1 : 0;
			for (final Type t : Type.getArgumentTypes(methodNode.desc)) {
				probeArrayPositionInLocalVariable += t.getSize();
			}

			InsnList getProbeArrayInsnList = new InsnList();
			getProbeArrayInsnList.add(new LdcInsnNode(name));
			getProbeArrayInsnList.add(new IntInsnNode(Opcodes.BIPUSH, mTotalMethodLineCount));
			getProbeArrayInsnList.add(new MethodInsnNode(
					Opcodes.INVOKESTATIC,
					"cn/hjf/ProbeDataStore",
					"getProbeArray",
					"(Ljava/lang/String;I)[Z",
					false
			));
			getProbeArrayInsnList.add(new VarInsnNode(Opcodes.ASTORE, probeArrayPositionInLocalVariable));

			methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), getProbeArrayInsnList);

			methodNode.maxLocals++;
		}
	}

	private void calculateTotalMethodLineCount() {
		for (MethodNode methodNode : methods) {
			int methodLineCount = 0;
			for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
				if (insnNode instanceof LineNumberNode) {
					methodLineCount++;
				}
			}
			mTotalMethodLineCount += methodLineCount;
			mMethodLineCountMap.put(methodNode.name + methodNode.signature, methodLineCount);
		}

		System.out.println("mTotalMethodLineCount : " + mTotalMethodLineCount);
	}

	//	private List<MethodAnalyser> mMethodAnalyserList = new ArrayList<>();
//
//	public ClassAnalyser(int api) {
//		super(api);
//	}
//
//	public ClassAnalyser(int api, ClassVisitor classVisitor) {
//		super(api, classVisitor);
//	}
//
//	@Override
//	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
//		final MethodVisitor methodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions);
//
//		if ("<init>".equals(name) || "<clinit>".equals(name)) {
//			return methodVisitor;
//		}
//
//		MethodAnalyser methodAnalyser = new MethodAnalyser(Opcodes.ASM7, access, name, descriptor, signature, exceptions);
//		methodAnalyser.setMethodVisitor(methodVisitor);
//
//		mMethodAnalyserList.add(methodAnalyser);
//
//		return methodAnalyser;
//	}
//
//	@Override
//	public void visitEnd() {
//		for (MethodAnalyser methodAnalyser : mMethodAnalyserList) {
//			methodAnalyser.accept(new MethodInstrumenter());
//		}
//	}
}
