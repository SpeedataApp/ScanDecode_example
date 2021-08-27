package com.scandecode_example.utils.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author echo
 * @date 2017/6/6
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Excel {
    //是否忽略该字段 默认忽略
    boolean ignore() default true;
    //导出表格该字段显示名称
    String name();
}
