package com.quinn.hunter.plugin.largeimage.bytecode.weaver;


import com.quinn.hunter.plugin.largeimage.bytecode.adapter.OkHttpClassAdapter;
import com.quinn.hunter.transform.asm.BaseWeaver;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

/**
 * ================================================
 *   
 * 创建日期：2020/4/5 7:29
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class OkHttpWeaver extends BaseWeaver {

    @Override
    protected ClassVisitor wrapClassWriter(ClassWriter classWriter) {
        return new OkHttpClassAdapter(classWriter);
    }
}
