package cn.hjf.plugin;

import cn.hjf.coverage.InstrumentClassVisitor;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class InstrumentTask extends DefaultTask {

    @TaskAction
    public void instrument() {
        System.out.println("----------------instrument---------------");


        try {

            String testClassPath = "I:\\as_workspace\\MyPluginTest\\app\\build\\intermediates\\classes\\debug\\cn\\hjf\\myplugintest\\MainActivity.class";

            byte[] classData = readClass(testClassPath);

            ClassReader classReader = new ClassReader(classData);
            InstrumentClassVisitor instrumentClassVisitor = new InstrumentClassVisitor(null);
            classReader.accept(instrumentClassVisitor, 0);

//            TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
//            classReader.accept(traceClassVisitor, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private byte[] readClass(String path) {
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
}
