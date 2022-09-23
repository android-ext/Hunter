package com.tencent.method.plugin.largeimage.transform;

import com.tencent.method.plugin.largeimage.weaver.UrlConnectionWeaver;
import com.tencent.plugin.base.HunterTransform;

import org.gradle.api.Project;

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2020/4/5 16:22
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class UrlConnectionTransform extends HunterTransform {

    public UrlConnectionTransform(Project project) {
        super(project);
        this.bytecodeWeaver = new UrlConnectionWeaver();
    }
}
