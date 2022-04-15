package io.github.zhdotm.banana.common.handler.biz;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.github.zhdotm.banana.common.constant.BootTypeEnum;
import io.github.zhdotm.banana.common.serializer.holder.GlobalSerializerHolder;
import io.github.zhdotm.banana.common.constant.AttributeKeyEnum;
import io.github.zhdotm.banana.common.protocol.BasicMessage;
import io.github.zhdotm.banana.common.protocol.command.RequestCommand;
import io.github.zhdotm.banana.common.protocol.command.ResponseCommand;
import io.github.zhdotm.banana.common.session.Session;
import io.github.zhdotm.banana.common.session.holder.ChannelSessionHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务处理器
 *
 * @author zhihao.mao
 */

@Slf4j
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

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BasicMessage.Message msg) throws Exception {
        BasicMessage.Header header = msg.getHeader();
        BasicMessage.Body body = msg.getBody();
        BasicMessage.StatusType status = body.getStatus();
        if (status != BasicMessage.StatusType.SUCCESS) {
            log.error(body.getInfo());
            return;
        }
        byte[] dataBytes = body.getData().toByteArray();
        if (ObjectUtil.isEmpty(dataBytes)) {
            return;
        }

        if (header.getType() == BasicMessage.HeaderType.REQ) {
            RequestCommand requestCommand = GlobalSerializerHolder.deserialize(dataBytes, RequestCommand.class);
            if (StrUtil.isBlank(requestCommand.getUniqueId())) {
                requestCommand.setUniqueId(header.getUniqueId());
            }
            ResponseCommand responseCommand = callReq(requestCommand);

            String sessionId = AttributeKeyEnum.SESSION_ID.getAttributeValue(ctx.channel());
            ChannelSessionHolder channelSessionHolder = ChannelSessionHolder.getInstance();
            Session session = channelSessionHolder.getSession(sessionId);
            session.sendCommand(responseCommand);
        }

        if (header.getType() == BasicMessage.HeaderType.RESP) {
            ResponseCommand responseCommand = GlobalSerializerHolder.deserialize(dataBytes, ResponseCommand.class);
            callResp(responseCommand);
        }

    }

    /**
     * 调用
     *
     * @param requestCommand 调用命令
     * @return 响应命令
     */
    protected abstract ResponseCommand callReq(RequestCommand requestCommand);

    /**
     * 返回
     *
     * @param responseCommand 返回命令
     */
    protected abstract void callResp(ResponseCommand responseCommand);

}
