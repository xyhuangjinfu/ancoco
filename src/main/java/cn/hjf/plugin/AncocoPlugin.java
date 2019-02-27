package cn.hjf.plugin;

import groovy.lang.Closure;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class AncocoPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.afterEvaluate(new Closure(this) {
            public void doCall() {
                Task transformClassesWithDexBuilderForDebug = project.getTasks().findByName("transformClassesWithDexBuilderForDebug");

//                InstrumentTask instrumentTask = project.getTasks().create("ancocoInstrument", InstrumentTask.class);
//
//                transformClassesWithDexBuilderForDebug.dependsOn(instrumentTask);

                Task compileDebugAidl = project.getTasks().findByName("compileDebugAidl");
                ManifestTask manifestTask = project.getTasks().create("manifestInstrument", ManifestTask.class);
                compileDebugAidl.dependsOn(manifestTask);
            }
        });
    }
}
