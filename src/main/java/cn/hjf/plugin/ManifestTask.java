package cn.hjf.plugin;

import com.android.build.gradle.AndroidConfig;
import com.android.build.gradle.AppExtension;
import com.android.build.gradle.api.AndroidSourceSet;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class ManifestTask extends DefaultTask {

    @TaskAction
    public void f() {
        Project project = getProject();
//        AppExtension appExtension = getProject().getExtensions().findByType(AppExtension.class);
        NamedDomainObjectContainer<AndroidSourceSet> namedDomainObjectContainer = project.container(AndroidSourceSet.class);
        namedDomainObjectContainer.whenObjectAdded(new Action<AndroidSourceSet>() {
            @Override
            public void execute(AndroidSourceSet androidSourceSet) {
                System.out.println("-----------------------------------------  ManifestTask : " + androidSourceSet);
            }
        });

        AndroidConfig androidConfig = project.getExtensions().getByType(AndroidConfig.class);
        System.out.println("-------------------  androidConfig : " + androidConfig);
        for (AndroidSourceSet sourceSet : androidConfig.getSourceSets()) {
            System.out.println(sourceSet.getManifest().getSrcFile());
            print(sourceSet.getManifest().getSrcFile());
        }

    }

    private void print(File file) {
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
