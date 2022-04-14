package io.github.zhdotm.banana.common.handler.beat;

import io.github.zhdotm.banana.common.constant.AttributeKeyEnum;
import io.github.zhdotm.banana.common.constant.ProtocolVersionEnum;
import io.github.zhdotm.banana.common.protocol.BasicMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;


/**
 * 服务端心跳处理器
 *
 * @author zhihao.mao
 */

@Slf4j
public class HeartBeatServerHandler extends IdleStateHandler {


    public HeartBeatServerHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
        super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        //2次空闲事件就关闭上下文
        if (!evt.isFirst()) {
            String clientIp = AttributeKeyEnum.CLIENT_IP.getAttributeValue(ctx.channel());
            long allIdleTimeInMillis = getAllIdleTimeInMillis();
            log.error("关闭客户端连接[{}]会话[{}]: {}", clientIp, AttributeKeyEnum.SESSION_ID.getAttributeValue(ctx.channel()), "心跳请求丢失超过" + allIdleTimeInMillis * 2 + "毫秒");
            ctx.close();
        }
        super.channelIdle(ctx, evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BasicMessage.Message message = (BasicMessage.Message) msg;
        BasicMessage.Header header = message.getHeader();
        //如果是心跳包
        if (header.getType() == BasicMessage.HeaderType.HEART_BEAT) {
            log.info(" 收到客户端[{}], 会话[{}], 心跳请求[{}]: {}", AttributeKeyEnum.CLIENT_IP.getAttributeValue(ctx.channel()), AttributeKeyEnum.SESSION_ID.getAttributeValue(ctx.channel()), header.getUniqueId(), header.getBeat());
            pong(ctx, header.getUniqueId());
        }

        super.channelRead(ctx, msg);
    }

    private void pong(ChannelHandlerContext ctx, String uniqueId) {
        BasicMessage.Header headerResp = BasicMessage
                .Header
                .newBuilder()
                .setVersion(ProtocolVersionEnum.ONE.getValue())
                .setType(BasicMessage.HeaderType.HEART_BEAT)
                .setUniqueId(uniqueId)
                .setBeat(BasicMessage.BeatType.PONG)
                .build();

        BasicMessage.Message messageResp = BasicMessage
                .Message
                .newBuilder()
                .setHeader(headerResp)
                .build();

        ctx.writeAndFlush(messageResp);
    }

}
