package io.github.zhdotm.banana.starter.web.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 配置
 *
 * @author zhihao.mao
 */

@Data
@Component
@ConfigurationProperties(prefix = BananaWebConfigProperties.PREFIX)
public class BananaWebConfigProperties {

    public static final String PREFIX = "banana";

    /**
     * 端口
     */
    private Integer port;

    /**
     * 创建会话超时（单位秒）
     */
    private Long createSessionTimeout = 5L;
}
