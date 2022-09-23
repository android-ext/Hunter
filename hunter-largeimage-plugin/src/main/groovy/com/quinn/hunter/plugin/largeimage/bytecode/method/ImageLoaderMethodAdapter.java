package com.quinn.hunter.plugin.largeimage.bytecode.method;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * ================================================
 *   
 * 创建日期：2020/4/3 11:49
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ImageLoaderMethodAdapter extends AdviceAdapter {

    /**
     * Creates a new {@link AdviceAdapter}.
     * @param mv     the method visitor to which this adapter delegates calls.
     * @param access the method's access flags
     * @param name   the method's name.
     * @param desc   the method's descriptor
     */
    public ImageLoaderMethodAdapter(MethodVisitor mv, int access, String name, String desc) {
        super(Opcodes.  ASM7, mv, access, name, desc);
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        mv.visitVarInsn(ALOAD, 5);
        mv.visitMethodInsn(INVOKESTATIC, "org/zzy/lib/largeimage/aop/imageloader/ImageLoaderHook", "process", "(Lcom/nostra13/universalimageloader/core/listener/ImageLoadingListener;)Lcom/nostra13/universalimageloader/core/listener/ImageLoadingListener;", false);
        mv.visitVarInsn(ASTORE, 5);
    }
}
