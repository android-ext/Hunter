package com.quinn.hunter.plugin.largeimage.transform;

import com.quinn.hunter.plugin.largeimage.bytecode.weaver.UrlConnectionWeaver;
import com.quinn.hunter.transform.HunterTransform;

import org.gradle.api.Project;

/**
 * ================================================
 *   
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
