package io.github.zhdotm.banana.common.listener;

import io.github.zhdotm.banana.common.constant.AttributeKeyEnum;
import io.github.zhdotm.banana.common.constant.BootTypeEnum;
import io.github.zhdotm.banana.common.session.holder.ChannelSessionHolder;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 关闭通道监听器
 *
 * @author zhihao.mao
 */

@Slf4j
@AllArgsConstructor
public class CloseChannelListener implements ChannelFutureListener {

    private final BootTypeEnum bootType;

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        String sessionId = AttributeKeyEnum.SESSION_ID.getAttributeValue(future.channel());
        
        log.warn("移除会话[{}]: {}", sessionId, ChannelSessionHolder.getInstance().removeSession(sessionId));
        if (bootType == BootTypeEnum.CLIENT) {
            log.warn("关闭通道成功: 服务端[{}], 会话[{}]", AttributeKeyEnum.SERVER_IP.getAttributeValue(future.channel()), sessionId);
            return;
        }
        if (bootType == BootTypeEnum.SERVER) {
            log.warn("关闭通道成功: 客户端[{}], 会话[{}]", AttributeKeyEnum.CLIENT_IP.getAttributeValue(future.channel()), sessionId);
        }
    }

}
