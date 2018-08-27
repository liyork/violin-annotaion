package com.wolf.classloader;

import com.wolf.annotation.Log;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;


class TestLoader extends ClassLoader {

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            byte[] data = loadClassData(name);
            if (data == null) {
                return super.loadClass(name, false);
            }
            return this.defineClass(name, data, 0, data.length);
        } catch (Exception e) {
            throw new ClassNotFoundException(e.getMessage(), e);
        }
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return this.findClass(name);
    }

    private byte[] loadClassData(String name) throws Exception {
        String cname = name.replace('.', '/') + ".class";
        URL uri = this.getClass().getClassLoader().getResource(cname);
        if (uri != null && "file".equalsIgnoreCase(Objects.requireNonNull(uri).getProtocol())) {
            try(InputStream is = uri.openStream()){
                CtClass ctClass = ClassPool.getDefault().makeClass(is, false);
                for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
                    processAnnotation(ctMethod);
                }
                System.out.println("--------load class data:" + ctClass.getName() + "--------");
                return ctClass.toBytecode();
            }
        }
        return null;
    }

    private void processAnnotation(CtMethod ctMethod) throws ClassNotFoundException, CannotCompileException {
        Log log = (Log) ctMethod.getAnnotation(Log.class);
        if (log != null) {
            ctMethod.insertBefore("System.out.println(\"[log] enter method " + ctMethod.getName() + "\");");
            ctMethod.insertAfter("System.out.println(\"[log] exit method " + ctMethod.getName() + "\");");
        }
    }
}


class Main{
    public static void main(String[] args) throws Exception {
        ClassLoader classLoader = new TestLoader();
        Class<?> clazz = classLoader.loadClass("com.wolf.classloader.Server");
        Runnable instance = (Runnable) clazz.getDeclaredConstructor().newInstance();
        instance.run();

        //使用新classloader进行隔离了
        System.out.println(clazz == Server.class);
        System.out.println(clazz.getClassLoader() == Server.class.getClassLoader());

    }
}