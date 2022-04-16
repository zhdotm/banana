package io.github.zhdotm.banana.common.handler.biz;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.github.zhdotm.banana.common.constant.BootTypeEnum;
import io.github.zhdotm.banana.common.exception.BananaBizException;
import io.github.zhdotm.banana.common.serializer.holder.GlobalSerializerHolder;
import io.github.zhdotm.banana.common.constant.AttributeKeyEnum;
import io.github.zhdotm.banana.common.protocol.BasicMessage;
import io.github.zhdotm.banana.common.protocol.command.RequestCommand;
import io.github.zhdotm.banana.common.protocol.command.ResponseCommand;
import io.github.zhdotm.banana.common.session.Session;
import io.github.zhdotm.banana.common.session.holder.ChannelSessionHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务处理器
 *
 * @author zhihao.mao
 */

@Slf4j
@ChannelHandler.Sharable
public abstract class BizInboundHandler extends SimpleChannelInboundHandler<BasicMessage.Message> {

    /**
     * 获取业务处理器名称
     *
     * @return 业务处理器名称
     */
    public abstract String getName();

    /**
     * 适用的引导类型
     *
     * @return 引导类型
     */
    public abstract BootTypeEnum getBootType();

    /**
     * 获取排序ID
     *
     * @return 排序ID
     */
    public abstract Integer getSortId();

    @SneakyThrows
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BasicMessage.Message msg) throws Exception {
        BasicMessage.Header header = msg.getHeader();
        BasicMessage.Body body = msg.getBody();

        byte[] dataBytes = body.getData().toByteArray();
        if (ObjectUtil.isEmpty(dataBytes)) {
            return;
        }

        if (header.getType() == BasicMessage.HeaderType.REQ) {
            RequestCommand requestCommand = GlobalSerializerHolder.deserialize(dataBytes, RequestCommand.class);
            if (StrUtil.isBlank(requestCommand.getUniqueId())) {
                requestCommand.setUniqueId(header.getUniqueId());
            }
            ResponseCommand responseCommand = request(requestCommand);
            if (ObjectUtil.isNotEmpty(responseCommand)) {
                String sessionId = AttributeKeyEnum.SESSION_ID.getAttributeValue(ctx.channel());
                ChannelSessionHolder channelSessionHolder = ChannelSessionHolder.getInstance();
                Session session = channelSessionHolder.getSession(sessionId);
                session.sendCommand(responseCommand);
            }
        }

        if (header.getType() == BasicMessage.HeaderType.RESP) {
            ResponseCommand responseCommand = GlobalSerializerHolder.deserialize(dataBytes, ResponseCommand.class);
            response(responseCommand);
        }

    }

    /**
     * 调用
     *
     * @param requestCommand 调用命令
     * @return 响应命令
     */
    protected abstract ResponseCommand request(RequestCommand requestCommand);

    /**
     * 返回
     *
     * @param responseCommand 返回命令
     */
    protected abstract void response(ResponseCommand responseCommand);

}
