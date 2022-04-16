package io.github.zhdotm.banana.common.handler.exception;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;


/**
 * 服务端异常处理
 *
 * @author zhihao.mao
 */

@Slf4j
public class BananaExceptionHandler extends BasicExceptionHandler {

    @Override
    public void doExceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

    }

}
