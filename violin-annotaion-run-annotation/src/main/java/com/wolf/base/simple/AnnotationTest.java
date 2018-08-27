package com.wolf.base.simple;

import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Description:
 *
 * <br/> Created on 10/07/2018 7:36 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class AnnotationTest {

    public static void main(String[] args) throws Exception {

//        testBaseUsage();
        showClassAnnotationInfo(UserAnnotation.class);
    }

    private static void testBaseUsage() {
        System.out.println(MyAnnotation.class.isAnnotation());

        Class<UserAnnotation> userAnnotationClass = UserAnnotation.class;
        boolean annotationPresent = userAnnotationClass.isAnnotationPresent(MyAnnotation.class);
        System.out.println(annotationPresent);

        if (annotationPresent) {
            MyAnnotation annotation = userAnnotationClass.getAnnotation(MyAnnotation.class);
            Class<? extends Annotation> annoClass = annotation.annotationType();//注解也是Class
            System.out.println(annoClass);
            System.out.println(annoClass.getSimpleName());
            System.out.println(annoClass.getDeclaredMethods());
            System.out.println(annotation.id() + "_" + annotation.msg());
        }

        try {
            Method test = userAnnotationClass.getMethod("test");
            boolean annotationPresent1 = test.isAnnotationPresent(MyAnnotation.class);
            if (annotationPresent1) {
                MyAnnotation annotation = test.getAnnotation(MyAnnotation.class);
                System.out.println(annotation.id() + "_" + annotation.msg());
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    //这个含有调用注解中所有属性的思想！
    public static void showClassAnnotationInfo(Class c) throws Exception {

        Method[] methods = c.getDeclaredMethods();

        Annotation[] annotations;

        for (Method method : methods) {

            annotations = method.getDeclaredAnnotations();

            StringBuilder sb = new StringBuilder();
            sb.append(method.getName() + "注解个数：" + annotations.length + "{");
            //$Proxy1@602
            for (Annotation an : annotations) {
                Class<? extends Annotation> aClass = an.annotationType();
                sb.append("[" + aClass.getSimpleName() + "(");
                Method[] meths = aClass.getDeclaredMethods();
                for (Method meth : meths) {
                    sb.append(meth.getName() + "=" + meth.invoke(an) + ", ");
                }
                sb.append(")], ");
            }
            sb.append("};");
            System.out.println(sb.toString());
        }
    }


    @Test
    public void actAnnotation() {
        AnnotationAct annotationAct = new AnnotationAct();
        Class annotationActClass = annotationAct.getClass();

        Method[] methods = annotationActClass.getMethods();
        for (Method method : methods) {
            boolean annotationPresent = method.isAnnotationPresent(MyAnnotation.class);
            if (annotationPresent) {
                try {
                    method.invoke(annotationAct, null);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
