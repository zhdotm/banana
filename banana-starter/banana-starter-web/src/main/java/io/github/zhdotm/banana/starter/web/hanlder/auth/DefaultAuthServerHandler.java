package io.github.zhdotm.banana.starter.web.hanlder.auth;

import io.github.zhdotm.banana.common.handler.auth.AuthServerHandler;
import io.github.zhdotm.banana.common.parser.AccessTokenParser;
import io.netty.channel.ChannelHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 默认认证处理器
 *
 * @author zhihao.mao
 */

@Slf4j
@AllArgsConstructor
@ChannelHandler.Sharable
public class DefaultAuthServerHandler extends AuthServerHandler {

    private final AccessTokenParser accessTokenParser;

    @Override
    public String getName() {
        return "defaultAuthServerHandler";
    }

    @Override
    public Map<String, Object> analyseAccessToken(String accessToken) {
        
        return accessTokenParser.parse(accessToken);
    }

}
