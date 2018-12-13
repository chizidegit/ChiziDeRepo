package com.chizi.java_lib;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by Chenll on 2018/12/11.
 */

public class InvocationHandlerImpl implements InvocationHandler {

    private Object mTarget;

    public InvocationHandlerImpl(Object target) {
        mTarget = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long start = System.currentTimeMillis();
        Object obj = method.invoke(mTarget, args);
        System.out.println(method.getName() + " cost time is:" + (System.currentTimeMillis() - start));
        return obj;
    }

}
