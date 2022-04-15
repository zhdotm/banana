package io.github.zhdotm.banana.annotation;

import java.lang.annotation.*;

/**
 * 远程服务调用注解
 *
 * @author zhihao.mao
 */

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface BananaRemoteApi {

    /**
     * 服务名称
     *
     * @return 服务名称
     */
    String serverName();

    /**
     * 服务地址
     *
     * @return 服务地址
     */
    String serverUrl() default "";

    /**
     * 超时时间（单位秒）
     *
     * @return 超时时间（单位秒）
     */
    long timeout() default 10;
}
