package com.graduate.zl.common.test;

import java.lang.reflect.Method;

/**
 * 测试内部类
 */
public class TestForInnerClass {

    public TestForInnerClass() {

    }

    public void method1() {

    }

    public void method2() {

    }

    public class innerClass0{
        public innerClass0(){

        }

        public void method0(){

        }
    }

    class innerClass1{
        public innerClass1(){

        }

        public int method3() {
            return 0;
        }

        private int method4() {
            return 0;
        }
    }

    private class innerClass2{
        public innerClass2(){

        }

        public int method5() {
            return 0;
        }

        private int method6() {
            return 0;
        }
    }

    private static class innerClass3{
        public innerClass3(){

        }

        public int method7() {
            return 0;
        }

        private int method8() {
            return 0;
        }
    }

    public static void main(String[] args) {
        String className = Thread.currentThread().getStackTrace()[1].getClassName();
        System.out.println(className);
        Class obj = null;
        try {
            obj = Class.forName(className);
            System.out.println(obj.getCanonicalName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Method[] methods = obj.getDeclaredMethods();
        Class[] classes = obj.getDeclaredClasses();
//        for(Method method : methods) {
//            System.out.println(method.getName());
//        }
//        System.out.println("--------------------");
//        for(Class clazz : classes) {
//            System.out.println(clazz.getName());
//        }

        Class innerClass = null;
        try {
             innerClass = Class.forName("com.graduate.zl.common.test.TestForInnerClass$innerClass2");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Method[] innermethods = innerClass.getDeclaredMethods();
        Class[] innerclasses = innerClass.getDeclaredClasses();
        for(Method method : innermethods) {
            System.out.println(method.getName());
        }
        System.out.println("--------------------");
        for(Class clazz : innerclasses) {
            System.out.println(clazz.getName());
        }
    }
}
