package com.mzh.banana.common.listener;

import cn.hutool.core.map.MapUtil;
import com.mzh.banana.common.constant.AttributeKeyEnum;
import com.mzh.banana.common.session.ChannelSession;
import com.mzh.banana.common.session.holder.ChannelSessionHolder;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 打开会话监听器
 *
 * @author zhihao.mao
 */

@Slf4j
@AllArgsConstructor
public class OpenSessionListener implements ChannelFutureListener {

    /**
     * 服务端IP
     */
    private final String serverIp;

    /**
     * 会话ID
     */
    private final String sessionId;

    /**
     * token
     */
    private final String accessToken;

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if (future.isSuccess()) {
            ChannelSession channelSession = new ChannelSession();
            Map<String, Object> authInfoMap = MapUtil.newHashMap();
            authInfoMap.put(AttributeKeyEnum.SERVER_IP.getValue(), serverIp);
            authInfoMap.put(AttributeKeyEnum.SESSION_ID.getValue(), sessionId);
            authInfoMap.put(AttributeKeyEnum.ACCESS_TOKEN.getValue(), accessToken);
            channelSession.setSessionId(sessionId);
            channelSession.setAuthInfoMap(authInfoMap);
            channelSession.setChannel((SocketChannel) future.channel());
            
            log.info("添加会话[{}]: {}", sessionId, ChannelSessionHolder.getInstance().addSession(channelSession));
        }
    }

}
