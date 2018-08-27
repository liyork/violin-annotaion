package com.wolf.javassist;

import com.wolf.annotation.Log;
import javassist.*;

//使用javassist模拟生成一个目标代理类进行代理
interface Handler {
    void before(String name);

    void after(String name);
}

class MyHandler implements Handler {
    @Override
    public void before(String name) {
        System.out.println("before method " + name + "...");
    }

    @Override
    public void after(String name) {
        System.out.println("after method " + name + "...");
    }
}

class UserService {
    @Log
    public void update1() {
        System.out.println("update 1...");
        this.update2();
    }

    @Log
    public void update2() {
        System.out.println("update 2...");
    }
}

class MyProxy {
    static <T> Class<T> newProxyClass(Class<T> clazz, Class<? extends Handler> handlerClass) throws Exception {
        CtClass superCtClass = ClassPool.getDefault().get(clazz.getName());
        CtClass proxyCtClass = ClassPool.getDefault().makeClass(clazz.getName() + "$Proxy", superCtClass);

        String fieldString = "private " + handlerClass.getName() + " _handler_ = new " + handlerClass.getName() + "();";
        CtField field = CtField.make(fieldString, proxyCtClass);
        proxyCtClass.addField(field);

        for (CtMethod src : superCtClass.getDeclaredMethods()) {
            CtMethod ctMethod = new CtMethod(src, proxyCtClass, new ClassMap());
            processAnnotation(src, ctMethod);
            proxyCtClass.addMethod(ctMethod);
        }

        return (Class<T>) proxyCtClass.toClass();
    }

    private static void processAnnotation(CtMethod src, CtMethod ctMethod) throws Exception {
        Log log = (Log) src.getAnnotation(Log.class);
        if (log != null) {
            ctMethod.insertBefore("_handler_.before(\"" + ctMethod.getName() + "\");");
            ctMethod.insertAfter("_handler_.after(\"" + ctMethod.getName() + "\");");
        }
    }
}


class Main {
    public static void main(String[] args) throws Exception {
        Class<UserService> proxyClass = MyProxy.newProxyClass(UserService.class, MyHandler.class);
        UserService us = proxyClass.getDeclaredConstructor().newInstance();
        us.update1();
    }
}