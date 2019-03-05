package cn.hjf.util;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class InsnListUtil {

    public static InsnList createGetProbesInsnList(String probeName, int allProbeCount, int probeArrayPositionInLocalVariable) {
        InsnList insnList = new InsnList();
        insnList.add(new LdcInsnNode(probeName));
        insnList.add(IntInsnUtil.getPushInsnNode(allProbeCount));
        insnList.add(new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "cn/hjf/ancoco/ProbeDataStore",
                "getProbeArray",
                "(Ljava/lang/String;I)[Z",
                false
        ));
        insnList.add(new VarInsnNode(Opcodes.ASTORE, probeArrayPositionInLocalVariable));
        return insnList;
    }

    public static InsnList createInsertProbeInsnList(int probeArrayPositionInLocalVariable, int probeIndex) {
        InsnList insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, probeArrayPositionInLocalVariable));
        insnList.add(IntInsnUtil.getPushInsnNode(probeIndex));
        insnList.add(new InsnNode(Opcodes.ICONST_1));
        insnList.add(new InsnNode(Opcodes.BASTORE));
        return insnList;
    }

    public static int probeArrayPositionInLocalVariable(MethodNode mn) {
        return mn.maxLocals;
    }
}
