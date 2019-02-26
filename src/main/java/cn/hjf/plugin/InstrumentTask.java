package cn.hjf.plugin;

import cn.hjf.coverage.ClassAnalyser;
import cn.hjf.coverage.InstrumentClassVisitor;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import java.io.*;

public class InstrumentTask extends DefaultTask {

	@TaskAction
	public void instrument() {
		System.out.println("----------------instrument---------------");

//		String classFileDir = getProject().getBuildDir() + "/intermediates/javac/debug/compileDebugJavaWithJavac/classes";
		String classFileDir = getProject().getBuildDir() + "/intermediates/classes/debug";

		//instrument code
		System.out.println("------instrument code------------ : " + classFileDir);
		instrumentPath(classFileDir);

		//copy code
		String path = classFileDir + "/cn/hjf/ancoco";
		String originPath = "/Users/huangjinfu/Downloads/cn/hjf/ancoco";

		File[] files = new File(originPath).listFiles();
		for (File file : files) {
			byte[] bytes = readClass(file.getAbsolutePath());
			writeClass(path + "/" + file.getName(), bytes);
		}

		System.out.println("------copy------------");

//        try {
//
//            String testClassPath = "I:\\as_workspace\\MyPluginTest\\app\\build\\intermediates\\classes\\debug\\cn\\hjf\\myplugintest\\MainActivity.class";
//
//            byte[] classData = readClass(testClassPath);
//
//            ClassReader classReader = new ClassReader(classData);
//            InstrumentClassVisitor instrumentClassVisitor = new InstrumentClassVisitor(null);
//            classReader.accept(instrumentClassVisitor, 0);
//
////            TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, new ASMifier(), new PrintWriter(System.out));
////            classReader.accept(traceClassVisitor, 0);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

	}


	private void instrumentPath(String path) {
		File file = new File(path);
		if (file.isDirectory()) {
			File[] fl = file.listFiles();
			for(File f : fl) {
				instrumentPath(f.getAbsolutePath());
			}
		} else {
//			if (!path.contains("android")) {
				byte[] classData = readClass(path);
				ClassAnalyser classAnalyser = new ClassAnalyser(Opcodes.ASM7);
				classAnalyser.setFilePath(path);
				ClassReader classReader = new ClassReader(classData);
				classReader.accept(classAnalyser, 0);
//			}
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

	public static void writeClass(String path, byte[] data) {
		File file = new File(path);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

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
