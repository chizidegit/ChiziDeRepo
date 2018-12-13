package com.chizi.java_lib;

/**
 * Created by Chenll on 2018/12/11.
 */

class OperateImpl implements Operate {

    @Override
    public void method1() {
        System.out.println("invoke method1");
        try {
            Thread.sleep(110);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void method2() {
        System.out.println("invoke method2");
        try {
            Thread.sleep(120);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void method3() {
        System.out.println("invoke method3");
        try {
            Thread.sleep(130);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
