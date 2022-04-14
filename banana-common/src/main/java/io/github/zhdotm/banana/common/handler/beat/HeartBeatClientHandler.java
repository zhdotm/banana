package io.github.zhdotm.banana.common.handler.beat;

import io.github.zhdotm.banana.common.constant.AttributeKeyEnum;
import io.github.zhdotm.banana.common.constant.ProtocolVersionEnum;
import io.github.zhdotm.banana.common.util.UniqueIdUtil;
import io.github.zhdotm.banana.common.protocol.BasicMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 客户端的心跳处理器
 *
 * @author zhihao.mao
 */

@Slf4j
public class HeartBeatClientHandler extends IdleStateHandler {

    /**
     * 心跳的时间间隔，单位为秒
     */
    private final Integer heartbeatInterval;

    public HeartBeatClientHandler(int heartbeatInterval, int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
        super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
        this.heartbeatInterval = heartbeatInterval;
        log.info("客户端设置心跳发送时间间隔: {}", heartbeatInterval + "秒");
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        //2次空闲事件就关闭上下文
        if (!evt.isFirst()) {
            String serverIp = AttributeKeyEnum.SERVER_IP.getAttributeValue(ctx.channel());
            long allIdleTimeInMillis = getAllIdleTimeInMillis();
            log.error("关闭服务端连接[{}], 会话[{}]: {}", serverIp, AttributeKeyEnum.SESSION_ID.getAttributeValue(ctx.channel()), "心跳响应丢失超过" + allIdleTimeInMillis * 2 + "毫秒");
            ctx.close();
            return;
        }
        super.channelIdle(ctx, evt);
    }

    /**
     * 在Handler业务处理器被加入到流水线时，开始发送心跳数据包
     *
     * @param ctx 通道上下文
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {

        schedulePing(ctx);
    }

    /**
     * 接收到服务器的心跳回写
     *
     * @param ctx 通道上下文
     * @param msg 消息
     */
    @SneakyThrows
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //判断类型
        BasicMessage.Message message = (BasicMessage.Message) msg;
        BasicMessage.Header header = message.getHeader();
        BasicMessage.HeaderType headerType = header.getType();
        if (headerType == BasicMessage.HeaderType.HEART_BEAT) {
            log.info(" 收到服务端[{}], 会话[{}], 心跳响应[{}]: {}", AttributeKeyEnum.SERVER_IP.getAttributeValue(ctx.channel()), AttributeKeyEnum.SESSION_ID.getAttributeValue(ctx.channel()), header.getUniqueId(), header.getBeat());
        }

        super.channelRead(ctx, msg);
    }

    private void schedulePing(ChannelHandlerContext ctx) {

        ctx.executor().schedule(() -> {
            if (ctx.channel().isActive()) {
                ping(ctx);
                //递归调用：提交下一个一次性的定时任务，发送下一次的心跳
                schedulePing(ctx);
            }
        }, heartbeatInterval, TimeUnit.SECONDS);
    }

    private void ping(ChannelHandlerContext ctx) {

        ping(ctx, UniqueIdUtil.getNextId());
    }

    private void ping(ChannelHandlerContext ctx, String uniqueId) {
        BasicMessage.Header headerResp = BasicMessage
                .Header
                .newBuilder()
                .setVersion(ProtocolVersionEnum.ONE.getValue())
                .setType(BasicMessage.HeaderType.HEART_BEAT)
                .setUniqueId(uniqueId)
                .setBeat(BasicMessage.BeatType.PING)
                .build();

        BasicMessage.Message messageResp = BasicMessage
                .Message
                .newBuilder()
                .setHeader(headerResp)
                .build();

        ctx.writeAndFlush(messageResp);
    }

}
