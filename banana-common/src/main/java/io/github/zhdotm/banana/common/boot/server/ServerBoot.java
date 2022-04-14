package io.github.zhdotm.banana.common.boot.server;

import io.github.zhdotm.banana.common.constant.BootTypeEnum;
import io.github.zhdotm.banana.common.handler.exception.ExceptionServerHandler;
import io.github.zhdotm.banana.common.codec.BasicMessageDecoder;
import io.github.zhdotm.banana.common.codec.BasicMessageEncoder;
import io.github.zhdotm.banana.common.constant.AttributeKeyEnum;
import io.github.zhdotm.banana.common.handler.beat.HeartBeatServerHandler;
import io.github.zhdotm.banana.common.listener.CloseChannelListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 默认服务端引导
 *
 * @author zhihao.mao
 */

@Data
@Slf4j
public abstract class ServerBoot implements BananaServerBoot {

    /**
     * 服务器端口
     */
    private int port = 8888;

    /**
     * 连接监听线程组
     */
    private EventLoopGroup boosGroup = new NioEventLoopGroup(1);

    /**
     * 传输处理线程组
     */
    private EventLoopGroup workGroup = new NioEventLoopGroup();

    /**
     * 启动引导器
     */
    private ServerBootstrap serverBootstrap = new ServerBootstrap();

    /**
     * 读空闲（单位秒）
     */
    private int readerIdleTimeSeconds = 0;

    /**
     * 写空闲（单位秒）
     */
    private int writerIdleTimeSeconds = 0;

    /**
     * 所有空闲（单位秒）
     */
    private int allIdleTimeSeconds = 30;

    @Override
    public void init() {
        //1、设置reactor 线程
        serverBootstrap.group(boosGroup, workGroup);
        //2、设置nio类型的channel
        serverBootstrap.channel(NioServerSocketChannel.class);
        //3、设置监听端口
        serverBootstrap.localAddress(new InetSocketAddress(port));
        //4、设置通道选项
        serverBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        serverBootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        //5、装配流水线
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            /**
             * 有连接到达时会创建一个channel
             * @param ch 通道
             * @throws Exception 异常
             */
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                // 管理pipeline中的Handler
                InetSocketAddress inetSocketAddress = ch.remoteAddress();
                AttributeKey<String> clientIpAttributeKey = AttributeKeyEnum.CLIENT_IP.getAttributeKey();
                ch.attr(clientIpAttributeKey).set(inetSocketAddress.getHostString() + ":" + inetSocketAddress.getPort());
                //添加解码器
                ch.pipeline().addLast(new BasicMessageDecoder());
                //添加编码器
                ch.pipeline().addLast(new BasicMessageEncoder());
                //添加认证处理器
                addAuthServerHandler(ch);
                //添加心跳处理器
                ch.pipeline().addLast(new HeartBeatServerHandler(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds));
                //添加自定义逻辑处理器
                addBizHandlers(ch);
                //添加异常处理器
                ch.pipeline().addLast(new ExceptionServerHandler());
                //监听通道关闭
                ChannelFuture channelFuture = ch.closeFuture();
                channelFuture.addListener(new CloseChannelListener(BootTypeEnum.SERVER));

            }
        });
    }

    @Override
    @SneakyThrows
    public void start() {
        init();
        try {
            //1、开始绑定server，通过调用sync同步方法阻塞直到绑定成功
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            //2、监听通道关闭事件，应用程序会一直等待，直到channel关闭
            ChannelFuture closeFuture = channelFuture.channel().closeFuture();
            closeFuture.sync();
        } finally {
            //3、优雅关闭EventLoopGroup，释放掉所有资源包括创建的线程
            workGroup.shutdownGracefully();
            boosGroup.shutdownGracefully();
        }
    }

}
