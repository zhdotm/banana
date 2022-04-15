package io.github.zhdotm.banana.common.handler.exception;

import io.github.zhdotm.banana.common.exception.BananaServerException;
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
        if (cause instanceof BananaServerException) {
            BananaServerException bananaServerException = (BananaServerException) cause;
            //是否需要关闭ctx
            if (bananaServerException.getIsNeedCloseCtx()) {
                log.error("服务端发生异常: ", cause);
                ctx.close();
                return;
            }
            log.error("服务端发生异常: ", cause);
        }
        //其他异常不主动关闭通道
        log.error("其他异常: ", cause);
    }

}
