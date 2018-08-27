package com.wolf.base.simple;

/**
 * Description:被MyAnnotation标记的方法会被检测
 * <br/> Created on 10/07/2018 8:11 PM
 *
 * @author 李超
 * @since 1.0.0
 */
public class AnnotationAct {

    @MyAnnotation(id = 1)
    public void test1() {
        System.out.println("test1");
    }

    @MyAnnotation(id = 2)
    public void test2() {
        int i = 1 / 0;
        System.out.println("test2");
    }

    public void test3() {
        int i = 1 / 0;
        System.out.println("test2");
    }
}
