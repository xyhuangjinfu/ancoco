package cn.hjf.test;

import cn.hjf.a.Instrumenter;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Test3 {

    public static void main(String[] args){
        try {
            String path = "/Users/huangjinfu/work/project/shanbay-words-android/app/build/intermediates/classes/debug/com/shanbay/words/startup/SplashActivity.class";
            byte[] b = Instrumenter.instrument(path);

            String pp = "/Users/huangjinfu/Downloads/Bbbbbbbb.class";
            FileOutputStream fos = new FileOutputStream(pp);
            fos.write(b);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
