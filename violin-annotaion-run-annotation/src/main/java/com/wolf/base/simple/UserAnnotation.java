package com.wolf.base.simple;

import java.lang.annotation.Retention;

/**
 * Description:
 * 注解用处：
 - 提供信息给编译器： 编译器可以利用注解来探测错误和警告信息
 - 编译阶段时的处理： 软件工具可以用来利用注解信息来生成代码、Html文档或者做其它相应处理。
 - 运行时的处理： 某些注解可以在程序运行的时候接受代码的提取
 * <br/> Created on 10/07/2018 7:37 PM
 *
 * @author 李超
 * @since 1.0.0
 */
@MyAnnotation(id = 1,msg = "abc")
public class UserAnnotation {

    @MyAnnotation(id = 2,msg = "qqq")
    public void test() {

    }
}
