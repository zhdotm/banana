package io.github.zhdotm.banana.starter.web.client;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.github.zhdotm.banana.common.boot.client.ClientBoot;
import io.github.zhdotm.banana.common.constant.BootTypeEnum;
import io.github.zhdotm.banana.common.creator.AccessTokenCreator;
import io.github.zhdotm.banana.common.exception.BananaCloseException;
import io.github.zhdotm.banana.common.handler.biz.BizInboundHandler;
import io.github.zhdotm.banana.common.handler.exception.BasicExceptionHandler;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 客户端
 *
 * @author zhihao.mao
 */

public class BananaWebClient extends ClientBoot {

    @Override
    public List<BizInboundHandler> getBizHandlers() {

        return SpringUtil.getBeansOfType(BizInboundHandler.class)
                .values()
                .stream()
                .filter(bizInboundHandler -> BootTypeEnum.CLIENT == bizInboundHandler.getBootType())
                .sorted(Comparator.comparingInt(BizInboundHandler::getSortId))
                .collect(Collectors.toList());
    }

    @Override
    public BasicExceptionHandler getExceptionHandler() {

        return SpringUtil.getBean(BasicExceptionHandler.class);
    }

    @Override
    public String createAccessToken(Map<String, Object> authInfoMap) {
        AccessTokenCreator accessTokenCreator = SpringUtil.getBean(AccessTokenCreator.class);
        if (ObjectUtil.isEmpty(accessTokenCreator)) {
            throw new BananaCloseException("获取accessToken生成器失败: accessTokenCreator不存在");
        }

        return accessTokenCreator.create(authInfoMap);
    }

}
