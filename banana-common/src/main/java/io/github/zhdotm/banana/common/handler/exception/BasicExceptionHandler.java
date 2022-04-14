package io.github.zhdotm.banana.common.handler.exception;

import cn.hutool.core.exceptions.ExceptionUtil;
import io.github.zhdotm.banana.common.constant.ProtocolVersionEnum;
import io.github.zhdotm.banana.common.exception.BananaServerException;
import io.github.zhdotm.banana.common.protocol.BasicMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 异常基础服务
 *
 * @author zhihao.mao
 */

public abstract class BasicExceptionHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //只有服务端异常有可能回写异常信息
        if (cause instanceof BananaServerException) {
            BananaServerException exp = (BananaServerException) cause;
            if (exp.getIsNeedWriteBackExceptionMessage()) {
                writeAndFlushException(exp.getUniqueId(), ctx, cause);
            }
        }

        doExceptionCaught(ctx, cause);
    }

    /**
     * 处理异常
     *
     * @param ctx   上下文
     * @param cause 异常
     * @throws Exception 异常
     */
    public abstract void doExceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception;

    /**
     * 回写异常
     *
     * @param uniqueId 消息ID
     * @param ctx      上下文
     * @param cause    异常
     */
    public void writeAndFlushException(String uniqueId, ChannelHandlerContext ctx, Throwable cause) {
        String exceptionMessage = ExceptionUtil.getMessage(cause);
        BasicMessage.Header header = BasicMessage
                .Header
                .newBuilder()
                .setVersion(ProtocolVersionEnum.ONE.getValue())
                .setType(BasicMessage.HeaderType.RESP)
                .setUniqueId(uniqueId)
                .build();
        BasicMessage.Body body = BasicMessage
                .Body
                .newBuilder()
                .setStatus(BasicMessage.StatusType.EXCEPTION)
                .setInfo(exceptionMessage)
                .build();
        BasicMessage.Message message = BasicMessage
                .Message
                .newBuilder()
                .setHeader(header)
                .setBody(body)
                .build();

        ctx.writeAndFlush(message);
    }

}
