package io.github.zhdotm.banana.common.boot.client;

import io.github.zhdotm.banana.common.boot.BananaBoot;
import io.github.zhdotm.banana.common.constant.BootTypeEnum;

import java.util.Map;

/**
 * 客户端引导
 *
 * @author zhihao.mao
 */

public interface BananaClientBoot extends BananaBoot {

    /**
     * 获取引导类型
     *
     * @return 引导类型
     */
    @Override
    default BootTypeEnum getBootType() {

        return BootTypeEnum.CLIENT;
    }

    /**
     * 连接服务
     *
     * @param host        host
     * @param port        port
     * @param accessToken accessToken
     * @param sessionId   sessionId
     */
    void connect(String host, Integer port, String accessToken, String sessionId);

    /**
     * 创建令牌
     *
     * @param authInfoMap 认证信息
     * @return 令牌
     */
    String createAccessToken(Map<String, Object> authInfoMap);

}
