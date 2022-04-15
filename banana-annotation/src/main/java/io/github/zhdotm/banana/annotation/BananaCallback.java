package io.github.zhdotm.banana.annotation;

import java.lang.annotation.*;

/**
 * 回调注解
 *
 * @author zhihao.mao
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface BananaCallback {

    /**
     * 回调方法的类
     *
     * @return 回调方法的类
     */
    Class<?> callbackClazz();

    /**
     * 方法名称
     *
     * @return 方法名称
     */
    String methodName();
}
