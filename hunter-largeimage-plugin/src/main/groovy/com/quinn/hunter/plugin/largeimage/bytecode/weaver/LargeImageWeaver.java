package com.quinn.hunter.plugin.largeimage.bytecode.weaver;

import com.quinn.hunter.plugin.largeimage.bytecode.adapter.LargeImageClassAdapter;
import com.quinn.hunter.transform.asm.BaseWeaver;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * ================================================
 *   
 * 创建日期：2020/3/31 21:24
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class LargeImageWeaver extends BaseWeaver {

    @Override
    protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
        return new LargeImageClassAdapter(classWriter);
    }
}
