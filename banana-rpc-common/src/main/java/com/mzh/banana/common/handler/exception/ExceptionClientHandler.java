package com.mzh.banana.common.handler.exception;

import com.mzh.banana.common.exception.BananaRpcClientException;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户端异常处理
 *
 * @author zhihao.mao
 */

@Slf4j
public class ExceptionClientHandler extends BasicExceptionHandler {

    @Override
    public void doExceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        //如果是客户端异常
        if (cause instanceof BananaRpcClientException) {
            BananaRpcClientException bananaRpcClientException = (BananaRpcClientException) cause;
            //是否需要关闭ctx
            if (bananaRpcClientException.getIsNeedCloseCtx()) {
                log.error("客户端发生异常: ", cause);
                ctx.close();
                return;
            }
            log.error("客户端发生异常: ", cause);
        }
        //其他异常不主动关闭通道
        log.error(cause.getMessage());
    }

}
