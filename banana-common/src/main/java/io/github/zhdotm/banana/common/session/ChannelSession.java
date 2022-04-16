package io.github.zhdotm.banana.common.session;

import com.google.protobuf.ByteString;
import io.github.zhdotm.banana.common.constant.ProtocolVersionEnum;
import io.github.zhdotm.banana.common.protocol.command.Command;
import io.github.zhdotm.banana.common.serializer.holder.GlobalSerializerHolder;
import io.github.zhdotm.banana.common.protocol.BasicMessage;
import io.netty.channel.socket.SocketChannel;
import lombok.Data;

import java.util.Map;

/**
 * 通道会话
 *
 * @author zhihao.mao
 */

@Data
public class ChannelSession implements Session {

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 认证信息
     */
    private Map<String, Object> authInfoMap;

    /**
     * 通道
     */
    private SocketChannel channel;

    @Override
    public void close() {

        channel.close();
    }

    @Override
    public <T extends Command> void sendCommand(T command) {
        String uniqueId = command.getUniqueId();
        String commandId = command.getCommandId();
        BasicMessage.HeaderType headerType = BasicMessage.HeaderType.valueOf(commandId);

        BasicMessage.Header header = BasicMessage
                .Header
                .newBuilder()
                .setVersion(ProtocolVersionEnum.ONE.getValue())
                .setType(headerType)
                .setUniqueId(uniqueId)
                .build();


        BasicMessage.Body body = BasicMessage.Body
                .newBuilder()
                .setData(ByteString.copyFrom(GlobalSerializerHolder.serialize(command)))
                .build();

        BasicMessage.Message message = BasicMessage
                .Message
                .newBuilder()
                .setHeader(header)
                .setBody(body)
                .build();

        sendMessage(message);
    }

    @Override
    public void sendMessage(BasicMessage.Message message) {

        channel.writeAndFlush(message);
    }

}
