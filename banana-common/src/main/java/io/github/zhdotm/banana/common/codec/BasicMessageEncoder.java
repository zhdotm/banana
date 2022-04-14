package io.github.zhdotm.banana.common.codec;

import io.github.zhdotm.banana.common.protocol.BasicMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器
 *
 * @author zhihao.mao
 */

public class BasicMessageEncoder extends MessageToByteEncoder<BasicMessage.Message> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, BasicMessage.Message message, ByteBuf byteBuf) throws Exception {
        //将对象转换为字节
        byte[] bytes = message.toByteArray();
        //读取消息的长度
        int length = bytes.length;
        //将消息长度写入，这里只用两个字节，最大为32767
        byteBuf.writeInt(length);
        //省略魔数、版本号的写入，写入的方式、写入长度是类似的
        //消息体中包含我们要发送的数据
        byteBuf.writeBytes(message.toByteArray());
    }

}
