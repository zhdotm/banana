package io.github.zhdotm.banana.starter.web.server;

import cn.hutool.extra.spring.SpringUtil;
import io.github.zhdotm.banana.common.boot.server.ServerBoot;
import io.github.zhdotm.banana.common.constant.BootTypeEnum;
import io.github.zhdotm.banana.common.handler.auth.AuthServerHandler;
import io.github.zhdotm.banana.common.handler.biz.BizInboundHandler;
import io.github.zhdotm.banana.common.handler.exception.BasicExceptionHandler;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务端
 *
 * @author zhihao.mao
 */

public class BananaWebServer extends ServerBoot {

    @Override
    public List<BizInboundHandler> getBizHandlers() {

        return SpringUtil.getBeansOfType(BizInboundHandler.class)
                .values()
                .stream()
                .filter(bizInboundHandler -> BootTypeEnum.SERVER == bizInboundHandler.getBootType())
                .sorted(Comparator.comparingInt(BizInboundHandler::getSortId))
                .collect(Collectors.toList());
    }

    @Override
    public BasicExceptionHandler getExceptionHandler() {

        return SpringUtil.getBean(BasicExceptionHandler.class);
    }

    @Override
    public AuthServerHandler getAuthServerHandler() {

        return SpringUtil.getBean(AuthServerHandler.class);
    }

}
