package cn.hjf.cc;

import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;

import java.util.LinkedList;
import java.util.Map;

public class MethodAnalyzer extends MethodNode {

    private ClassNode mOwner;
    private int mTotalLineCount;
    private LinkedList<Integer> mBranchLineList = new LinkedList<>();

    public MethodAnalyzer() {
    }

    public MethodAnalyzer(int api) {
        super(api);
    }

    public MethodAnalyzer(int access, String name, String descriptor, String signature, String[] exceptions) {
        super(access, name, descriptor, signature, exceptions);
    }

    public MethodAnalyzer(int api, int access, String name, String descriptor, String signature, String[] exceptions) {
        super(api, access, name, descriptor, signature, exceptions);
    }

    /**
     * ***************************************************************************************************************
     * //
     * ***************************************************************************************************************
     */

    public void setOwner(ClassNode owner) {
        mOwner = owner;
    }

    public int getTotalLineCount() {
        return mTotalLineCount;
    }

    public LinkedList<Integer> getBranchLineList() {
        return mBranchLineList;
    }

    /**
     * ***************************************************************************************************************
     * <p>
     * ***************************************************************************************************************
     */

    @Override
    public void visitEnd() {
        //all instructions
        AbstractInsnNode[] insnNodeArray = instructions.toArray();

        //calculate total line count
        for (AbstractInsnNode insnNode : insnNodeArray) {
            if (insnNode instanceof LineNumberNode) {
                mTotalLineCount++;
            }
        }

        //calculate branch line number
        try {
            BranchAnalyzer<BasicValue> branchAnalyzer = new BranchAnalyzer<>(new BasicInterpreter());
            branchAnalyzer.analyze(mOwner.name, this);

            Map<Integer, LinkedList<Integer>> map = branchAnalyzer.getBranchMap();

            for (Map.Entry<Integer, LinkedList<Integer>> e : map.entrySet()) {
                LinkedList<Integer> list = e.getValue();
                for (Integer insIndex : list) {
                    AbstractInsnNode insnNode = insnNodeArray[insIndex];

                    if (insnNode instanceof LabelNode) {
                        for (int i = insIndex + 1; i < insnNodeArray.length; i++) {
                            AbstractInsnNode node = insnNodeArray[i];
                            if (node instanceof LineNumberNode) {
                                LineNumberNode lineNumberNode = (LineNumberNode) node;
                                mBranchLineList.add(lineNumberNode.line);
                                break;
                            }
                        }
                    } else {
                        for (int i = insIndex; i >= 0; i--) {
                            AbstractInsnNode node = insnNodeArray[i];
                            if (node instanceof LineNumberNode) {
                                LineNumberNode lineNumberNode = (LineNumberNode) node;
                                mBranchLineList.add(lineNumberNode.line);
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}