package cn.hjf.plugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class InstrumentTask extends DefaultTask {

    @TaskAction
    public void instrument() {
        System.out.println("----------------instrument---------------");
    }
}
