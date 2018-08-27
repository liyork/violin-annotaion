package com.wolf.base.simple;

import javax.xml.bind.Element;
import java.lang.annotation.*;

/**
 * Description:
 * <br/> Created on 10/07/2018 7:36 PM
 *
 * @author 李超
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented//将注解中的元素包含到 Javadoc 中去
@Target({ElementType.TYPE,ElementType.METHOD})
//@Repeatable()
public @interface MyAnnotation {

    int id();
    String msg() default "";
}
