package com.quinn.hunter.plugin.timing;

import com.android.build.gradle.AppExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import java.util.Collections;

/**
 * Created by Quinn on 25/02/2017.
 */
public class TimingHunterPlugin implements Plugin<Project> {

    @SuppressWarnings("NullableProblems")
    @Override
    public void apply(Project project) {
        AppExtension appExtension = (AppExtension)project.getProperties().get("android");

        //将自定义Transform添加到编译流程中
        appExtension.registerTransform(new LargeImageTransform(project), Collections.EMPTY_LIST);
        //添加OkHttp
        appExtension.registerTransform(new OkHttpTransform(project),Collections.EMPTY_LIST);
        //添加UrlConnection
        appExtension.registerTransform(new UrlConnectionTransform(project),Collections.EMPTY_LIST);
    }

}
