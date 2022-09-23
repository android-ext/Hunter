package com.quinn.hunter.plugin.largeimage;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import java.util.Collections;

import com.quinn.hunter.plugin.largeimage.transform.LargeImageTransform;
import com.quinn.hunter.plugin.largeimage.transform.OkHttpTransform;
import com.quinn.hunter.plugin.largeimage.transform.UrlConnectionTransform;

/**
 * Created by Quinn on 25/02/2017.
 */
public class LargeImagePlugin implements Plugin<Project> {

    @SuppressWarnings("NullableProblems")
    @Override
    public void apply(Project project) {
        AppExtension appExtension = (AppExtension)project.getProperties().get("android");

        //创建自定义扩展
        project.getExtensions().create("largeImageMonitor", LargeImageExtension.class);
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project params) {
                LargeImageExtension extension = params.getExtensions().getByType(LargeImageExtension.class);
                Config.getInstance().setLargeImageExtension(extension);
            }
        });

        //将自定义Transform添加到编译流程中
        appExtension.registerTransform(new LargeImageTransform(project), Collections.EMPTY_LIST);
        //添加OkHttp
        appExtension.registerTransform(new OkHttpTransform(project), Collections.EMPTY_LIST);
        //添加UrlConnection
        appExtension.registerTransform(new UrlConnectionTransform(project),Collections.EMPTY_LIST);
    }

}
