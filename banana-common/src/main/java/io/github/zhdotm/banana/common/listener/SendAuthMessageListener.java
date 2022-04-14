package io.github.zhdotm.banana.common.listener;

import io.github.zhdotm.banana.common.exception.BananaClientException;
import io.github.zhdotm.banana.common.protocol.BasicMessage;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * 发送认证消息监听器
 *
 * @author zhihao.mao
 */

@Slf4j
@AllArgsConstructor
public class SendAuthMessageListener implements ChannelFutureListener {

    /**
     * 认证消息
     */
    private final Supplier<BasicMessage.Message> authMessageSupplier;

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if (future.isSuccess()) {
            BasicMessage.Message message = authMessageSupplier.get();
            BasicMessage.Header header = message.getHeader();
            if (header.getType() != BasicMessage.HeaderType.AUTHENTICATION) {
                throw new BananaClientException("发送认证消息失败: 消息类型有误", Boolean.TRUE);
            }
            future.channel().writeAndFlush(message);
        }
    }

}
