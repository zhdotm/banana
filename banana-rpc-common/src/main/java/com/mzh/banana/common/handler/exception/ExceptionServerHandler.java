package com.mzh.banana.common.handler.exception;

import com.mzh.banana.common.exception.BananaRpcServerException;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;


/**
 * 服务端异常处理
 *
 * @author zhihao.mao
 */

@Slf4j
public class ExceptionServerHandler extends BasicExceptionHandler {

    @Override
    public void doExceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //如果是服务端异常
        if (cause instanceof BananaRpcServerException) {
            BananaRpcServerException bananaRpcServerException = (BananaRpcServerException) cause;
            //是否需要关闭ctx
            if (bananaRpcServerException.getIsNeedCloseCtx()) {
                log.error("服务端发生异常: ", cause);
                ctx.close();
                return;
            }
            log.error("服务端发生异常: ", cause);
        }
        //其他异常不主动关闭通道
        log.error(cause.getMessage());
    }

}
