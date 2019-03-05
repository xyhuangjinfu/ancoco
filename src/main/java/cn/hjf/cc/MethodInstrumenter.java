package cn.hjf.cc;

import cn.hjf.util.IntInsnUtil;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;

public class MethodInstrumenter extends MethodNode {

    private ClassNode mClassNode;
    private MethodVisitor mMethodVisitor;
    private MethodAnalyzer mMethodAnalyzer;
    private int mProbeIndex;
    private int mTotalLineCount;

    public MethodInstrumenter() {
    }

    public MethodInstrumenter(int api) {
        super(api);
    }

    public MethodInstrumenter(int access, String name, String descriptor, String signature, String[] exceptions) {
        super(access, name, descriptor, signature, exceptions);
    }

    public MethodInstrumenter(int api, int access, String name, String descriptor, String signature, String[] exceptions) {
        super(api, access, name, descriptor, signature, exceptions);
    }

    @Override
    public void visitEnd() {
//        if (!"onUserOffline".equals(name)) {
//            return;
//        }

        //no instruction
        if (instructions.size() == 0) {
            return;
        }

        System.out.println("------------------------ " + name + " ---------------------");
        for (AbstractInsnNode node : instructions.toArray()) {
            if (node instanceof LineNumberNode) {
                System.out.print(((LineNumberNode) node).line);
                System.out.print(" - ");
                System.out.print(node);
                System.out.println();
            } else if (node instanceof JumpInsnNode) {
                System.out.print("   ");
                System.out.print(node);
                System.out.print(" , ");
                System.out.print(((JumpInsnNode) node).label);
                System.out.println();
            } else {
                System.out.print("   ");
                System.out.print(node);
                System.out.println();
            }
        }

        //insert position
        List<AbstractInsnNode> insertPositionList = getInsertPositionList();

        //get probe array
        instructions.insertBefore(instructions.getFirst(), createGetProbesInsnList());

        //insert probe
        for (AbstractInsnNode insertNode : insertPositionList) {
            instructions.insert(insertNode, createInsertProbeInsnList());
        }

        //change local
        maxLocals++;

        //change stack
        maxStack = maxStack + 3;

//        System.out.println("---instrumented--");
//        for (AbstractInsnNode node : instructions.toArray()) {
//            if (node instanceof LineNumberNode) {
//                System.out.print(((LineNumberNode) node).line);
//                System.out.print(" - ");
//                System.out.print(node);
//                System.out.println();
//            } else if (node instanceof JumpInsnNode) {
//                System.out.print(node);
//                System.out.print(" , ");
//                System.out.print(((JumpInsnNode) node).label);
//                System.out.println();
//            } else {
//                System.out.print(node);
//                System.out.println();
//            }
//        }

        accept(mMethodVisitor);
    }

    /**
     * ***************************************************************************************************************
     * <p>
     * ***************************************************************************************************************
     */

    public void setMethodAnalyzer(MethodAnalyzer methodAnalyzer) {
        mMethodAnalyzer = methodAnalyzer;
    }

    public void setMethodVisitor(MethodVisitor methodVisitor) {
        mMethodVisitor = methodVisitor;
    }

    public void setClassNode(ClassNode classNode) {
        mClassNode = classNode;
    }

    public void setTotalLineCount(int totalLineCount) {
        mTotalLineCount = totalLineCount;
    }

    public void setProbeIndex(int probeIndex) {
        mProbeIndex = probeIndex;
    }

    /**
     * ***************************************************************************************************************
     * <p>
     * ***************************************************************************************************************
     */

    private InsnList createGetProbesInsnList() {
        InsnList insnList = new InsnList();
        insnList.add(new LdcInsnNode(mClassNode.name));
        insnList.add(IntInsnUtil.getPushInsnNode(mTotalLineCount));
        insnList.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "cn/hjf/ancoco/ProbeDataStore",
                "getProbeArray",
                "(Ljava/lang/String;I)[Z",
                false
        ));
        insnList.add(new VarInsnNode(Opcodes.ASTORE, probeArrayPositionInLocalVariable()));
        return insnList;
    }

    private InsnList createInsertProbeInsnList() {
        InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, probeArrayPositionInLocalVariable()));
        insnList.add(IntInsnUtil.getPushInsnNode(mProbeIndex++));
        insnList.add(new InsnNode(Opcodes.ICONST_1));
        insnList.add(new InsnNode(Opcodes.BASTORE));
        return insnList;
    }

    private int probeArrayPositionInLocalVariable() {
        return maxLocals;
    }

    private List<AbstractInsnNode> getInsertPositionList() {
        List<AbstractInsnNode> list = new ArrayList<>();
        AbstractInsnNode[] insnNodeArray = instructions.toArray();
        for (int i = 0; i < insnNodeArray.length; i++) {
            AbstractInsnNode node = insnNodeArray[i];
            if (node instanceof LineNumberNode) {
                AbstractInsnNode insertNode = null;
                for (int j = i + 1; j < insnNodeArray.length; j++) {
                    AbstractInsnNode insn = insnNodeArray[j];
                    if (isNotLastLabelInsn(insn)
                            || isUnConditionJump(insn)
//                            || isReturnInsn(insn)
                            ) {
                        insertNode = insn.getPrevious();
                        break;
                    }
                }
                if (insertNode != null) {
                    list.add(insertNode);
                }
            }
        }
        return list;
    }

    private boolean isNotLastLabelInsn(AbstractInsnNode abstractInsnNode) {
        if (abstractInsnNode instanceof LabelNode) {
            LabelNode labelNode = (LabelNode) abstractInsnNode;
            if (labelNode.getNext() != null) {
                return true;
            }
        }
        return false;
    }

    private boolean isReturnInsn(AbstractInsnNode abstractInsnNode) {
        if (abstractInsnNode instanceof InsnNode) {
            InsnNode insnNode = (InsnNode) abstractInsnNode;
            if (insnNode.getOpcode() == Opcodes.IRETURN
                    || insnNode.getOpcode() == Opcodes.LRETURN
                    || insnNode.getOpcode() == Opcodes.FRETURN
                    || insnNode.getOpcode() == Opcodes.DRETURN
                    || insnNode.getOpcode() == Opcodes.ARETURN
                    || insnNode.getOpcode() == Opcodes.RETURN) {
                return true;
            }
        }
        return false;
    }

    private boolean isUnConditionJump(AbstractInsnNode abstractInsnNode) {
        if (abstractInsnNode instanceof JumpInsnNode) {
            JumpInsnNode insnNode = (JumpInsnNode) abstractInsnNode;
            if (insnNode.getOpcode() == Opcodes.GOTO
                    || insnNode.getOpcode() == Opcodes.TABLESWITCH
                    || insnNode.getOpcode() == Opcodes.LOOKUPSWITCH) {
                return true;
            }
        }
        return false;
    }
}
