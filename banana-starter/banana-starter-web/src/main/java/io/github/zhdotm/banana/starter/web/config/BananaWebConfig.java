package io.github.zhdotm.banana.starter.web.config;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import io.github.zhdotm.banana.common.creator.AccessTokenCreator;
import io.github.zhdotm.banana.common.exception.BananaServerException;
import io.github.zhdotm.banana.common.handler.auth.AuthServerHandler;
import io.github.zhdotm.banana.common.parser.AccessTokenParser;
import io.github.zhdotm.banana.starter.web.hanlder.auth.DefaultAuthServerHandler;
import io.github.zhdotm.banana.starter.web.hanlder.biz.ClientBizInboundHandler;
import io.github.zhdotm.banana.starter.web.hanlder.biz.ServerBizInboundHandler;
import io.github.zhdotm.banana.starter.web.config.properties.BananaWebConfigProperties;
import io.github.zhdotm.banana.starter.web.processor.BananaProcessor;
import io.github.zhdotm.banana.starter.web.runner.BananaWebRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 配置
 *
 * @author zhihao.mao
 */

@Configuration
@Import({BananaWebConfigProperties.class, SpringUtil.class})
public class BananaWebConfig {

    @Bean
    @ConditionalOnMissingBean(AccessTokenCreator.class)
    public AccessTokenCreator accessTokenCreator() {

        return new AccessTokenCreator() {
            @Override
            public String create(Map<String, Object> authInfoMap) {
                authInfoMap = Optional.ofNullable(authInfoMap).orElseGet(HashMap::new);

                return JSONUtil.toJsonStr(authInfoMap);
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(AccessTokenParser.class)
    public AccessTokenParser accessTokenParser() {

        return new AccessTokenParser() {
            @Override
            public Map<String, Object> parse(String accessToken) {
                if (!JSONUtil.isJson(accessToken)) {
                    throw new BananaServerException("解析accessToken失败: 非JSON", Boolean.TRUE);
                }

                return JSONUtil.parseObj(accessToken);
            }
        };
    }

    @Bean
    public AuthServerHandler authServerHandler(AccessTokenParser accessTokenParser) {

        return new DefaultAuthServerHandler(accessTokenParser);
    }

    @Bean
    public ClientBizInboundHandler clientBizInboundHandler() {

        return new ClientBizInboundHandler();
    }

    @Bean
    public ServerBizInboundHandler serverBizInboundHandler() {

        return new ServerBizInboundHandler();
    }

    @Bean
    public BananaWebRunner bananaWebRunner(BananaWebConfigProperties bananaWebConfigProperties) {

        return new BananaWebRunner(bananaWebConfigProperties);
    }

    @Bean
    public BananaProcessor bananaProcessor() {

        return new BananaProcessor();
    }
}
