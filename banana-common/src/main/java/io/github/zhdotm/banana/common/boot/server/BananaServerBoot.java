package io.github.zhdotm.banana.common.boot.server;

import io.github.zhdotm.banana.common.boot.BananaBoot;
import io.github.zhdotm.banana.common.constant.BootTypeEnum;
import io.github.zhdotm.banana.common.handler.auth.AuthServerHandler;
import io.netty.channel.socket.SocketChannel;


/**
 * 服务端引导
 *
 * @author zhihao.mao
 */

public interface BananaServerBoot extends BananaBoot {

    /**
     * 获取引导类型
     *
     * @return 引导类型
     */
    @Override
    default BootTypeEnum getBootType() {

        return BootTypeEnum.SERVER;
    }

    /**
     * 启动
     */
    void start();


    /**
     * 获取认证处理器
     *
     * @return 认证处理器
     */
    AuthServerHandler getAuthServerHandler();

    /**
     * 添加权限处理器
     *
     * @param ch 通道
     */
    default void addAuthServerHandler(SocketChannel ch) {
        AuthServerHandler authServerHandler = getAuthServerHandler();
        ch.pipeline().addLast(authServerHandler.getName(), authServerHandler);
    }

}
