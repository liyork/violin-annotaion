package com.wolf.annotation;

import java.lang.annotation.*;

/**
 * <p> Description: 缓存结果集
 * <p/>
 * Date: 2015/12/22
 * Time: 8:56
 *
 * @author 李超
 * @version 1.0
 * @since 1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheResult {

    String domain() ;

    String[] key() default {};

    long TTL() default 30*60;//过期时间 单位秒
}
