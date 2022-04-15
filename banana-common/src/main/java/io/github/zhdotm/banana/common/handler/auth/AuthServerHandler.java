package io.github.zhdotm.banana.common.handler.auth;

import cn.hutool.core.util.ObjectUtil;
import io.github.zhdotm.banana.common.constant.AttributeKeyEnum;
import io.github.zhdotm.banana.common.protocol.BasicMessage;
import io.github.zhdotm.banana.common.session.ChannelSession;
import io.github.zhdotm.banana.common.session.holder.ChannelSessionHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 身份认证处理器
 *
 * @author zhihao.mao
 */

@Slf4j
public abstract class AuthServerHandler extends SimpleChannelInboundHandler<BasicMessage.Message> {

    /**
     * 获取权限处理器名称
     *
     * @return 权限处理器名称
     */
    public abstract String getName();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BasicMessage.Message msg) throws Exception {
        BasicMessage.Header header = msg.getHeader();
        String clientIp = AttributeKeyEnum.CLIENT_IP.getAttributeValue(ctx.channel());
        String sessionId = header.getSessionId();
        String accessToken = header.getAccessToken();
        if (header.getType() != BasicMessage.HeaderType.AUTHENTICATION) {
            log.error("关闭客户端连接[{}], 会话[{}]: {}", clientIp, AttributeKeyEnum.SESSION_ID.getAttributeValue(ctx.channel()), "请求类型非认证类型");
            ctx.close();
            return;
        }

        if (!header.hasAccessToken()) {
            log.error("关闭客户端连接[{}], 会话[{}]: {}", clientIp, AttributeKeyEnum.SESSION_ID.getAttributeValue(ctx.channel()), "未携带accessToken信息");
            ctx.close();
            return;
        }

        Map<String, Object> authInfoMap = analyseAccessToken(accessToken);
        if (ObjectUtil.isEmpty(authInfoMap)) {
            log.error("关闭客户端连接[{}], 会话[{}]: {}", clientIp, AttributeKeyEnum.SESSION_ID.getAttributeValue(ctx.channel()), "解析accessToken失败");
            ctx.close();
            return;
        }

        authInfoMap.forEach((key, value) -> {
            AttributeKey<Object> attributeKey = AttributeKey.valueOf(key);
            Attribute<Object> attr = ctx.channel().attr(attributeKey);
            attr.set(value);
        });
        AttributeKeyEnum.ACCESS_TOKEN.getAttribute(ctx.channel()).set(accessToken);
        AttributeKeyEnum.SESSION_ID.getAttribute(ctx.channel()).set(sessionId);

        log.info("客户端认证成功, 会话[{}]: {}", AttributeKeyEnum.SESSION_ID.getAttributeValue(ctx.channel()), AttributeKeyEnum.CLIENT_IP.getAttributeValue(ctx.channel()));
        ctx.pipeline().remove(AuthServerHandler.class);
        
        ChannelSession channelSession = new ChannelSession();
        channelSession.setChannel((SocketChannel) ctx.channel());
        channelSession.setSessionId(sessionId);
        ChannelSessionHolder.getInstance().addSession(channelSession);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("通道移除认证处理器, 会话[{}]: {}", AttributeKeyEnum.SESSION_ID.getAttributeValue(ctx.channel()), AttributeKeyEnum.CLIENT_IP.getAttributeValue(ctx.channel()));
        super.handlerRemoved(ctx);
    }

    /**
     * 解析token信息
     *
     * @param accessToken token
     * @return 授权信息
     */
    public abstract Map<String, Object> analyseAccessToken(String accessToken);

}
