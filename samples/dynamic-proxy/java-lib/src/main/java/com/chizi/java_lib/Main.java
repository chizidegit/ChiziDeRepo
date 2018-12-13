package com.chizi.java_lib;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class Main {

    public static void main(String[] args) {
        Operate target = new OperateImpl();
        InvocationHandler handler = new InvocationHandlerImpl(target);
        Operate proxy = (Operate) Proxy.newProxyInstance(Operate.class.getClassLoader(), new Class[]{Operate.class}, handler);

        proxy.method1();
        proxy.method2();
        proxy.method3();

        boolean success = ProxyUtils.saveProxyClass("$Proxy0.class", proxy.getClass().getSimpleName(), proxy.getClass().getInterfaces());
        if (success) {
            System.out.println("save proxy class file success");
        }
    }

}
