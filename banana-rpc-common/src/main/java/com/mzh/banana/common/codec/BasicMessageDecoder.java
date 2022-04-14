package com.mzh.banana.common.codec;

import com.mzh.banana.common.protocol.BasicMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 解码器
 *
 * @author zhihao.mao
 */

public class BasicMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //标记一下当前的读指针readIndex的位置
        byteBuf.markReaderIndex();
        //判断包头的长度
        if (byteBuf.readableBytes() < 4) {
            return;
        }
        //读取传送过来的消息的长度
        int length = byteBuf.readInt();
        //长度如果小于0
        if (length < 0) {
            //非法数据，关闭连接
            channelHandlerContext.close();
        }
        //可读字节少于预期消息长度
        if (length > byteBuf.readableBytes()) {
            //重置读取位置
            byteBuf.resetReaderIndex();
            return;
        }

        byte[] array;
        //堆缓冲
        if (byteBuf.hasArray()) {
            ByteBuf slice = byteBuf.slice();
            array = slice.array();
        } else {
            //直接缓冲
            array = new byte[length];
            byteBuf.readBytes(array, 0, length);
        }
        //字节转成Protobuf对象
        BasicMessage.Message outMsg = BasicMessage.Message.parseFrom(array);
        if (outMsg != null) {
            //将Protobuf实例加入出站List容器
            list.add(outMsg);
        }
    }

}
