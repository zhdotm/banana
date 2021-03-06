package io.github.zhdotm.banana.common.boot.client;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.github.zhdotm.banana.common.codec.BasicMessageDecoder;
import io.github.zhdotm.banana.common.codec.BasicMessageEncoder;
import io.github.zhdotm.banana.common.constant.AttributeKeyEnum;
import io.github.zhdotm.banana.common.constant.BootTypeEnum;
import io.github.zhdotm.banana.common.constant.ProtocolVersionEnum;
import io.github.zhdotm.banana.common.exception.BananaCloseException;
import io.github.zhdotm.banana.common.handler.beat.HeartBeatClientHandler;
import io.github.zhdotm.banana.common.listener.CloseChannelListener;
import io.github.zhdotm.banana.common.listener.SendAuthMessageListener;
import io.github.zhdotm.banana.common.protocol.BasicMessage;
import io.github.zhdotm.banana.common.session.ChannelSession;
import io.github.zhdotm.banana.common.session.holder.ChannelSessionHolder;
import io.github.zhdotm.banana.common.util.SessionUtil;
import io.github.zhdotm.banana.common.util.UniqueIdUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


/**
 * 默认客户端引导
 *
 * @author zhihao.mao
 */

@Slf4j
@Data
public abstract class ClientBoot implements BananaClientBoot {

    /**
     * 服务器ip地址
     */
    private String host;

    /**
     * 服务器端口
     */
    private Integer port;

    /**
     * 引导
     */
    private Bootstrap bootstrap = new Bootstrap();

    /**
     * 接收连接和处理连接
     */
    private EventLoopGroup group = new NioEventLoopGroup(1);

    /**
     * 心跳间隙时间
     */
    private int heartbeatInterval = 10;

    /**
     * 读空闲时间
     */
    private int readerIdleTimeSeconds = 0;

    /**
     * 写空闲时间
     */
    private int writerIdleTimeSeconds = 0;

    /**
     * 空闲时间
     */
    private int allIdleTimeSeconds = 30;

    /**
     * 认证令牌
     */
    private String accessToken;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 是否需要解锁会话锁
     */
    private Boolean isNeedUnlockSessionLock = Boolean.FALSE;

    @Override
    public BootTypeEnum getBootType() {

        return BootTypeEnum.CLIENT;
    }

    @SneakyThrows
    @Override
    public void init() {
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        if (StrUtil.isBlank(host) || ObjectUtil.isEmpty(port)) {
            throw new BananaCloseException("初始化客户端引导失败: 服务器ip地址和端口不能为空");
        }
        if (StrUtil.isBlank(sessionId)) {
            throw new BananaCloseException("初始化客户端引导失败: sessionId不能为空");
        }
        bootstrap.remoteAddress(host, port);

        // 设置通道初始化
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            /**
             * 初始化连接通道
             * @param ch 通道
             */
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.attr(AttributeKeyEnum.SERVER_IP.getAttributeKey()).set(host + ":" + port);
                ch.attr(AttributeKeyEnum.SESSION_ID.getAttributeKey()).set(sessionId);
                ch.attr(AttributeKeyEnum.ACCESS_TOKEN.getAttributeKey()).set(accessToken);
                ch.pipeline().addLast(new BasicMessageDecoder());
                //添加
                ch.pipeline().addLast(new BasicMessageEncoder());
                //添加心跳处理器
                ch.pipeline().addLast(new HeartBeatClientHandler(heartbeatInterval, readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds));
                //添加自定义逻辑处理器
                addBizHandlers(ch);
                //添加异常处理器
                addExceptionHandler(ch);

            }
        });
    }

    @SneakyThrows
    @Override
    public void connect(String host, Integer port, String accessToken, String sessionId) {
        this.host = host;
        this.port = port;
        this.accessToken = accessToken;
        this.sessionId = sessionId;

        String serverIp = host + ":" + port;

        init();
        try {
            //异步发起连接
            ChannelFuture channelFuture = bootstrap.connect();
            //添加发送认证消息监听器
            channelFuture.addListener(new SendAuthMessageListener(() -> createAuthenticationMessage(accessToken, sessionId)));
            //添加开启会话监听器
            ChannelFuture closeFuture = channelFuture.channel().closeFuture();
            //关闭通道时移除会话
            closeFuture.addListener(new CloseChannelListener(BootTypeEnum.CLIENT));
            ChannelSession channelSession = createChannelSession(serverIp, (SocketChannel) channelFuture.channel());
            log.info("添加会话[{}]: {}", sessionId, ChannelSessionHolder.getInstance().addSession(channelSession));
            if (isNeedUnlockSessionLock) {
                SessionUtil.unlock(sessionId);
            }
            // 阻塞
            closeFuture.sync();

        } finally {
            group.shutdownGracefully();
        }
    }

    /**
     * 生成认证消息
     *
     * @param accessToken token
     * @return 认证消息
     */
    public BasicMessage.Message createAuthenticationMessage(String accessToken, String sessionId) {
        BasicMessage.Header headerResp = BasicMessage
                .Header
                .newBuilder()
                .setVersion(ProtocolVersionEnum.ONE.getValue())
                .setType(BasicMessage.HeaderType.AUTHENTICATION)
                .setUniqueId(UniqueIdUtil.getNextId("auth"))
                .setAccessToken(accessToken)
                .setSessionId(sessionId)
                .build();

        return BasicMessage
                .Message
                .newBuilder()
                .setHeader(headerResp)
                .build();
    }

    private ChannelSession createChannelSession(String serverIp, SocketChannel ch) {
        ChannelSession channelSession = new ChannelSession();
        Map<String, Object> authInfoMap = MapUtil.newHashMap();
        authInfoMap.put(AttributeKeyEnum.SERVER_IP.getValue(), serverIp);
        authInfoMap.put(AttributeKeyEnum.SESSION_ID.getValue(), sessionId);
        authInfoMap.put(AttributeKeyEnum.ACCESS_TOKEN.getValue(), accessToken);
        channelSession.setSessionId(sessionId);
        channelSession.setAuthInfoMap(authInfoMap);
        channelSession.setChannel(ch);

        return channelSession;
    }

}
