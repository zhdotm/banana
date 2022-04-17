package io.github.zhdotm.banana.starter.web.hanlder.exception;

import io.github.zhdotm.banana.common.handler.exception.BasicExceptionHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;


/**
 * 服务端异常处理
 *
 * @author zhihao.mao
 */

@Slf4j
@ChannelHandler.Sharable
public class DefaultExceptionHandler extends BasicExceptionHandler {

    @Override
    public String getName() {

        return "defaultExceptionHandler";
    }

    @Override
    public Integer getSortId() {

        return Integer.MAX_VALUE;
    }

    @Override
    public void doExceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

}
