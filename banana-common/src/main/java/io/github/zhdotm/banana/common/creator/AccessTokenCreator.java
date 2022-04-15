package io.github.zhdotm.banana.common.creator;

import java.util.Map;

/**
 * 创建器
 *
 * @author zhihao.mao
 */

public interface AccessTokenCreator {

    /**
     * 创建accessToken
     *
     * @param authInfoMap 认证信息
     * @return accessToken
     */
    String create(Map<String, Object> authInfoMap);
}
