package com.lushihao.model.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogHandler {
    /**
     * 日志描述信息
     */
    String description() default "";
}
