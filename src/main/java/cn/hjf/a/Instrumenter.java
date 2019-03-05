package cn.hjf.a;

import cn.hjf.util.InsnListUtil;
import cn.hjf.util.IntInsnUtil;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Instrumenter {

    public static byte[] instrument(String originClassPath) {
        try {
            ClassReader cr = new ClassReader(new FileInputStream(originClassPath));

            ClassNode cn = new ClassNode();

            cr.accept(cn, 0);

            List<MethodNode> methodNodeList = cn.methods;

            Map<MethodNode, IProbeDetector> probeDetectorMap = new HashMap<>();

            for (MethodNode mn : methodNodeList) {
                IProbeDetector probeDetector = new BranchProbeDetector(cn, mn);
                probeDetectorMap.put(mn, probeDetector);
            }

            int allProbeCount = 0;
            for (IProbeDetector probeDetector : probeDetectorMap.values()) {
                allProbeCount += probeDetector.getInsertPosition().size();
            }

            int probeIndex = 0;
            for (MethodNode mn : methodNodeList) {
//                if (!"zzz".equals(mn.name)) {
//                    continue;
//                }

                for (AbstractInsnNode node : mn.instructions.toArray()) {
                    if (node instanceof JumpInsnNode) {
                        System.out.println(node + " - " + ((JumpInsnNode) node).label);
                        continue;
                    }
                    System.out.println(node);
                }

                if (mn.instructions.size() == 0) {
                    continue;
                }

                //insert probe
                IProbeDetector probeDetector = probeDetectorMap.get(mn);
                //do noting
                if (probeDetector.getInsertPosition().isEmpty()) {
                    continue;
                }

                AbstractInsnNode[] insnNodeArray = mn.instructions.toArray();
                for (Integer probePosition : probeDetector.getInsertPosition()) {
                    System.out.println(probePosition);
                    mn.instructions.insert(insnNodeArray[probePosition], InsnListUtil.createInsertProbeInsnList(InsnListUtil.probeArrayPositionInLocalVariable(mn), probeIndex++));
                }

                //get probe array
                mn.instructions.insertBefore(mn.instructions.getFirst(), InsnListUtil.createGetProbesInsnList(cn.name, allProbeCount, InsnListUtil.probeArrayPositionInLocalVariable(mn)));

                //change local
                mn.maxLocals++;

                //change stack
                mn.maxStack = mn.maxStack + 3;

                for (AbstractInsnNode node : mn.instructions.toArray()) {
                    System.out.println(node);
                }
            }

            ClassWriter cw = new ClassWriter(0);

            cn.accept(cw);

            return cw.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
