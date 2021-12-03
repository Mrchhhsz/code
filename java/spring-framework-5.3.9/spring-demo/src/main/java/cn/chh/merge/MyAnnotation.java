package cn.chh.merge;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: chh
 * @Version: 1.0
 * @Date: 2021/10/16 11:08
 * @Since: 1.0
 * @Package: cn.chh.merge
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {
	int count() default 0;
}
