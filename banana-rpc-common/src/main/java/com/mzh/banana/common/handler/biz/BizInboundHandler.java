package com.mzh.banana.common.handler.biz;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.mzh.banana.common.constant.AttributeKeyEnum;
import com.mzh.banana.common.protocol.BasicMessage;
import com.mzh.banana.common.protocol.command.RequestCommand;
import com.mzh.banana.common.protocol.command.ResponseCommand;
import com.mzh.banana.common.serializer.holder.GlobalSerializerHolder;
import com.mzh.banana.common.session.Session;
import com.mzh.banana.common.session.holder.ChannelSessionHolder;
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
            ResponseCommand responseCommand = doReq(requestCommand);

            String sessionId = AttributeKeyEnum.SESSION_ID.getAttributeValue(ctx.channel());
            ChannelSessionHolder channelSessionHolder = ChannelSessionHolder.getInstance();
            Session session = channelSessionHolder.getSession(sessionId);
            session.sendCommand(responseCommand);
        }

        if (header.getType() == BasicMessage.HeaderType.RESP) {
            ResponseCommand responseCommand = GlobalSerializerHolder.deserialize(dataBytes, ResponseCommand.class);
            doResp(responseCommand);
        }

    }

    /**
     * 调用
     *
     * @param requestCommand 调用命令
     * @return 响应命令
     */
    protected abstract ResponseCommand doReq(RequestCommand requestCommand);

    /**
     * 返回
     *
     * @param responseCommand 返回命令
     */
    protected abstract void doResp(ResponseCommand responseCommand);

}
