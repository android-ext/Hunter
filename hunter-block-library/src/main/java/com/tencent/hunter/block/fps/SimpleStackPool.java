package com.tencent.monitor.fps;


import java.util.Stack;

/**
 * Created by verus on 2017/4/26.
 * <p>
 * 非线程安全,只允许主进程调用。
 * <p>
 */

public class SimpleStackPool<T> implements Pools.Pool<T> {
    private Stack<T> mPool;

    public SimpleStackPool() {
        mPool = new Stack<T>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T acquire() {
        if (mPool.size() > 0) {
            return mPool.pop();
        }
        return null;
    }

    @Override
    public boolean release(T instance) {
        if (mPool.contains(instance)) {
            return false;
        }
        mPool.push(instance);
        return true;
    }
}
