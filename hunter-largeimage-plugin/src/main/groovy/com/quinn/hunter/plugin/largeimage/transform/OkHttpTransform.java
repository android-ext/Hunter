package com.tencent.method.plugin.largeimage.transform;

import com.android.build.api.transform.Context;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformOutputProvider;
import com.tencent.method.plugin.largeimage.weaver.OkHttpWeaver;
import com.tencent.plugin.base.HunterTransform;
import com.tencent.plugin.base.RunVariant;

import org.gradle.api.Project;

import java.io.IOException;
import java.util.Collection;

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2020/4/5 7:28
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class OkHttpTransform extends HunterTransform {

    public OkHttpTransform(Project project) {
        super(project);
        this.bytecodeWeaver = new OkHttpWeaver();
    }

    @Override
    public void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental);
    }

    @Override
    protected RunVariant getRunVariant() {
        return super.getRunVariant();
    }
}
