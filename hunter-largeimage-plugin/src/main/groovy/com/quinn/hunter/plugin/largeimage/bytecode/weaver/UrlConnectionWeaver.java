package com.tencent.method.plugin.largeimage.weaver;

import com.tencent.method.plugin.largeimage.adapter.UrlConnectionClassAdapter;
import com.tencent.plugin.base.asm.BaseWeaver;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2020/4/5 16:24
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class UrlConnectionWeaver extends BaseWeaver {

    @Override
    protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
        return new UrlConnectionClassAdapter(classWriter);
    }
}
