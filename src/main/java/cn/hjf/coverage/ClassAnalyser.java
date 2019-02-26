package cn.hjf.coverage;

import cn.hjf.test.InstrumentTest;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Map;

public class ClassAnalyser extends ClassNode {

	private int mTotalMethodLineCount;
	private Map<String, Integer> mMethodLineCountMap = new HashMap<>();
	private int mProbeIndex = 0;
	private String mFilePath;

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

//		InstrumentTest.writeClass("/Users/huangjinfu/study/idea-workspace/ancoco/out/production/classes/cn/hjf/test/Test1.class", classWriter.toByteArray());
		InstrumentTest.writeClass(mFilePath, classWriter.toByteArray());
		System.out.println("write class : " + mFilePath);

	}

	private void insertProbeForMethod() {

		for (MethodNode methodNode : methods) {
//			int probeArrayPositionInLocalVariable = (Opcodes.ACC_STATIC & methodNode.access) == 0 ? 1 : 0;
//			int probeArrayPositionInLocalVariable = 0;
//			for (final Type t : Type.getArgumentTypes(methodNode.desc)) {
//				probeArrayPositionInLocalVariable += t.getSize();
//			}

			if (methodNode.instructions.size() == 0) {
				continue;
			}

			int probeArrayPositionInLocalVariable = methodNode.maxLocals;

			System.out.println("----" + methodNode.name + " , probeArrayPositionInLocalVariable : " + probeArrayPositionInLocalVariable);

			InsnList getProbeArrayInsnList = new InsnList();
			getProbeArrayInsnList.add(new LdcInsnNode(name));
			getProbeArrayInsnList.add(new IntInsnNode(Opcodes.BIPUSH, mTotalMethodLineCount));
			getProbeArrayInsnList.add(new MethodInsnNode(
					Opcodes.INVOKESTATIC,
					"cn/hjf/ancoco/ProbeDataStore",
					"getProbeArray",
					"(Ljava/lang/String;I)[Z",
					false
			));
			getProbeArrayInsnList.add(new VarInsnNode(Opcodes.ASTORE, probeArrayPositionInLocalVariable));

			methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), getProbeArrayInsnList);

			methodNode.maxLocals++;

			//------

			for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
				if (insnNode instanceof LineNumberNode) {
					/**
					 * methodVisitor.visitVarInsn(ALOAD, 1);
					 methodVisitor.visitInsn(ICONST_0);
					 methodVisitor.visitInsn(ICONST_1);
					 methodVisitor.visitInsn(BASTORE);
					 */
					InsnList insnList = new InsnList();
					insnList.add(new VarInsnNode(Opcodes.ALOAD, probeArrayPositionInLocalVariable));
					insnList.add(new IntInsnNode(Opcodes.BIPUSH, mProbeIndex++));
					insnList.add(new InsnNode(Opcodes.ICONST_1));
					insnList.add(new InsnNode(Opcodes.BASTORE));

					methodNode.instructions.insert(insnNode, insnList);
				}
			}

			methodNode.maxStack = methodNode.maxStack + 10;

		}
	}

	private void calculateTotalMethodLineCount() {
		for (MethodNode methodNode : methods) {
			int methodLineCount = 0;
			System.out.println("----------------------" + methodNode.name + "---------------------");
			for (AbstractInsnNode insnNode : methodNode.instructions.toArray()) {
				System.out.println("    " + insnNode);
				if (insnNode instanceof LineNumberNode) {
					methodLineCount++;
				}
			}
			mTotalMethodLineCount += methodLineCount;
			mMethodLineCountMap.put(methodNode.name + methodNode.signature, methodLineCount);
		}

		System.out.println("mTotalMethodLineCount : " + mTotalMethodLineCount);
	}

	public void setFilePath(String filePath) {
		mFilePath = filePath;
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
