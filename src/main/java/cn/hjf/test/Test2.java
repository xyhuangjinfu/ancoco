package cn.hjf.test;

import cn.hjf.cc.ClassAnalyzer;
import cn.hjf.cc.ClassInstrumenter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Test2 {

    public static void main(String[] args) {
        try {
            String path = "/Users/huangjinfu/work/project/shanbay-words-android/app/build/intermediates/classes/debug/com/shanbay/words/startup/SplashActivity.class";
            FileInputStream fis = new FileInputStream(path);
            ClassReader cr = new ClassReader(fis);
            ClassWriter cw = new ClassWriter(0);

            ClassAnalyzer ca = new ClassAnalyzer(Opcodes.ASM7);
            cr.accept(ca, 0);

            ClassInstrumenter ci = new ClassInstrumenter(Opcodes.ASM7);
            ci.setClassAnalyzer(ca);
            ca.accept(ci);

            ci.accept(cw);

            String writePath = "/Users/huangjinfu/Downloads/Aaaaaaaaaaa.class";
            FileOutputStream fos = new FileOutputStream(writePath);
            fos.write(cw.toByteArray());
            fos.flush();
            fos.close();

            fis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}