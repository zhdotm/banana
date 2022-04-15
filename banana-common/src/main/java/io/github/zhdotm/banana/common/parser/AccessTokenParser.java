package io.github.zhdotm.banana.common.parser;

import java.util.Map;

/**
 * 解析器
 *
 * @author zhihao.mao
 */

public interface AccessTokenParser {

    /**
     * 解析token
     *
     * @param accessToken token
     * @return 认证信息
     */
    Map<String, Object> parse(String accessToken);
}
