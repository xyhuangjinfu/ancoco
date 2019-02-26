package cn.hjf.test;

import cn.hjf.coverage.ClassAnalyser;
import cn.hjf.coverage.InstrumentClassVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.*;

public class InstrumentTest {

    public static void main(String[] args) {
        String readPath = "/Users/huangjinfu/Downloads/HomeActivity.class";
//        String readPath = "/Users/huangjinfu/study/idea-workspace/ancoco/out/production/classes/cn/hjf/test/Worker1.class";
        String writePath = readPath;
        byte[] data = readClass(readPath);
        ClassReader classReader = new ClassReader(data);


        if (true) {
//            ClassWriter classWriter = new ClassWriter(0);

            ClassAnalyser classAnalyser = new ClassAnalyser(Opcodes.ASM7);

            classReader.accept(classAnalyser, 0);

//            writeClass(writePath, classWriter.toByteArray());
        } else {
            TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
            classReader.accept(traceClassVisitor, 0);
        }


    }


    public static byte[] readClass(String path) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            FileInputStream fis = new FileInputStream(path);
            int readCount = -1;
            byte[] readBuffer = new byte[1024];


            while (true) {
                readCount = fis.read(readBuffer);
                if (readCount == -1) {
                    break;
                }
                bos.write(readBuffer, 0, readCount);
            }

            fis.close();

            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
            }
        }

        return null;
    }

    public static void writeClass(String path, byte[] data) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
