package cn.hjf.a;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;

import java.util.*;

public class BranchProbeDetector implements IProbeDetector {

    private final ClassNode mClassNode;
    private final MethodNode mMethodNode;
    private List<Integer> mInsertPositionList;

    public BranchProbeDetector(ClassNode classNode, MethodNode methodNode) {
        mClassNode = classNode;
        mMethodNode = methodNode;
    }

    @Override
    public String getName() {
        return "branch";
    }

    @Override
    public List<Integer> getInsertPosition() {
        //read cache
        if (mInsertPositionList != null) {
            return mInsertPositionList;
        }

        mInsertPositionList = new ArrayList<>();

        BranchAnalyzer<BasicValue> branchAnalyzer = new BranchAnalyzer<>(new BasicInterpreter());

        try {
            branchAnalyzer.analyze(mClassNode.name, mMethodNode);
            Map<Integer, LinkedList<Integer>> map = branchAnalyzer.getRealBranchMap();

            Set<Integer> branchSet = new HashSet<>();
            for (LinkedList<Integer> branches : map.values()) {
                branchSet.addAll(branches);
            }

            mInsertPositionList.addAll(branchSet);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mInsertPositionList;
    }

    private int getLineInfo(AbstractInsnNode[] abstractInsnNodes, int insnIndex) {
        for (int i = insnIndex; i >= 0; i--) {
            AbstractInsnNode node = abstractInsnNodes[i];
            if (node instanceof LineNumberNode) {
                return ((LineNumberNode) node).line;
            }
        }
        throw new RuntimeException("find line error");
    }
}
