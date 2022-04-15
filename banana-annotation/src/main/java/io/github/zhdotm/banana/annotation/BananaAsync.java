package io.github.zhdotm.banana.annotation;

import java.lang.annotation.*;

/**
 * 异步注解
 *
 * @author zhihao.mao
 */

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface BananaAsync {
}
