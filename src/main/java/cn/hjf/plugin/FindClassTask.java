package cn.hjf.plugin;

import com.android.build.gradle.tasks.factory.AndroidJavaCompile;
import org.gradle.api.DefaultTask;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskAction;

public class FindClassTask extends DefaultTask {


	@TaskAction
	public void f() {
		AndroidJavaCompile androidJavaCompile = (AndroidJavaCompile) getProject().getTasks().findByName("AndroidJavaCompile");
	}
}
