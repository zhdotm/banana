package io.github.zhdotm.banana.common.boot;

import io.github.zhdotm.banana.common.constant.BootTypeEnum;
import io.github.zhdotm.banana.common.handler.biz.BizInboundHandler;
import io.netty.channel.socket.SocketChannel;

import java.util.List;


/**
 * 引导
 *
 * @author zhihao.mao
 */

public interface BananaBoot {

    /**
     * 获取引导类型
     *
     * @return 引导
     */
    BootTypeEnum getBootType();

    /**
     * 初始化参数
     */
    void init();

    /**
     * 获取业务处理器列表
     *
     * @return 业务处理器
     */
    List<BizInboundHandler> getBizHandlers();

    /**
     * 添加业务
     *
     * @param ch 通道
     */
    default void addBizHandlers(SocketChannel ch) {
        List<BizInboundHandler> bizHandlers = getBizHandlers();
        bizHandlers.forEach(bizInboundHandler -> {
            String name = bizInboundHandler.getName();
            ch.pipeline().addLast(name, bizInboundHandler);
        });
    }

}
