package io.github.zhdotm.banana.common.handler.exception;

import io.github.zhdotm.banana.common.exception.BananaCloseException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.SneakyThrows;

/**
 * 异常基础服务
 *
 * @author zhihao.mao
 */

public abstract class BasicExceptionHandler extends ChannelInboundHandlerAdapter {

    @SneakyThrows
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        doExceptionCaught(ctx, cause);
        if (cause instanceof BananaCloseException) {
            ctx.close();
        }
        throw cause;
    }

    /**
     * 处理异常
     *
     * @param ctx   上下文
     * @param cause 异常
     * @throws Exception 异常
     */
    public abstract void doExceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception;

}
