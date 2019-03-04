package cn.hjf.test;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;
import org.objectweb.asm.tree.analysis.Frame;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class BranchTest {

	public static void main(String[] args) {

		try {
			String path = "/Users/huangjinfu/work/project/shanbay-words-android/app/build/intermediates/classes/debug/com/shanbay/words/startup/SplashActivity.class";
			FileInputStream fis = new FileInputStream(path);
			ClassReader cr = new ClassReader(fis);
			ClassWriter cw = new ClassWriter(0);
			MyClassNode mcv = new MyClassNode(Opcodes.ASM7);
			mcv.setCv(cw);
			cr.accept(mcv, 0);

			String writePath = "/Users/huangjinfu/Downloads/Xxx.class";
			FileOutputStream fos = new FileOutputStream(writePath);
			fos.write(cw.toByteArray());
			fos.flush();
			fos.close();

			fis.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static class MyClassNode extends ClassNode {

		ClassVisitor cv;

		public void setCv(ClassVisitor cv) {
			this.cv = cv;
		}

		public MyClassNode(int api) {
			super(api);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
			MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
			MyMethodNode mmv = new MyMethodNode(Opcodes.ASM7, access, name, descriptor, signature, exceptions);
			mmv.setMv(mv);
			mmv.setOwner(this.name);
			return mmv;
		}

		@Override
		public void visitEnd() {
			accept(cv);
		}
	}

	static class MyMethodNode extends MethodNode {
		MethodVisitor mv;
		String owner;

		public void setOwner(String owner) {
			this.owner = owner;
		}

		public void setMv(MethodVisitor mv) {
			this.mv = mv;
		}

		public MyMethodNode(int api, int access, String name, String descriptor, String signature, String[] exceptions) {
			super(api, access, name, descriptor, signature, exceptions);
		}

		@Override
		public void visitEnd() {
			System.out.println("--------------------------" + name + "--------------------------------");
			try {
				MyAnalyzer<BasicValue> analyzer = new MyAnalyzer<>(new BasicInterpreter());
				analyzer.analyze(owner, this);

//				Frame<BasicValue>[] frames = analyzer.getFrames();
				AbstractInsnNode[] insnNodes = instructions.toArray();
				for (int i = 0; i < insnNodes.length; i++) {
					System.out.println(i + " - " + insnNodes[i]);
				}

				Map<Integer, LinkedList<Integer>> map = analyzer.getBranch();
				System.out.println(map);

				for (Map.Entry<Integer, LinkedList<Integer>> e : map.entrySet()) {
					LinkedList<Integer> list = e.getValue();
					for (Integer insIndex : list) {
						AbstractInsnNode insnNode = insnNodes[insIndex];

						if (insnNode instanceof LabelNode) {
							for (int i = insIndex + 1; i < insnNodes.length; i++) {
								AbstractInsnNode node = insnNodes[i];
								if (node instanceof LineNumberNode) {
									LineNumberNode lineNumberNode = (LineNumberNode) node;
									System.out.println("line : " + lineNumberNode.line);
									break;
								}
							}
						} else {
							for (int i = insIndex; i >= 0; i--) {
								AbstractInsnNode node = insnNodes[i];
								if (node instanceof LineNumberNode) {
									LineNumberNode lineNumberNode = (LineNumberNode) node;
									System.out.println("line : " + lineNumberNode.line);
									break;
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			accept(mv);
		}
	}

	static class MyAnalyzer<V extends Value> extends Analyzer {

		private Map<Integer, LinkedList<Integer>> mListMap = new HashMap<>();

		/**
		 * Constructs a new {@link Analyzer}.
		 *
		 * @param interpreter the interpreter to use to symbolically interpret the bytecode instructions.
		 */
		public MyAnalyzer(Interpreter interpreter) {
			super(interpreter);
		}

		@Override
		protected void newControlFlowEdge(int insnIndex, int successorIndex) {
//			System.out.println(insnIndex + " - " + successorIndex);

			LinkedList<Integer> list = mListMap.get(insnIndex);

			if (list == null) {
				list = new LinkedList();
				mListMap.put(insnIndex, list);
			}

			list.add(successorIndex);
		}

		public Map<Integer, LinkedList<Integer>> getBranch() {
			Iterator<Map.Entry<Integer, LinkedList<Integer>>> iterator = mListMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<Integer, LinkedList<Integer>> e = iterator.next();
				if (e.getValue().size() <= 1) {
					iterator.remove();
				}
			}
			return mListMap;
		}
	}
}
