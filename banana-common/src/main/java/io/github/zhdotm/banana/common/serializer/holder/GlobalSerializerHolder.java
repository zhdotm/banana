package io.github.zhdotm.banana.common.serializer.holder;

import cn.hutool.core.util.ObjectUtil;
import io.github.zhdotm.banana.common.serializer.Serializer;
import io.github.zhdotm.banana.common.serializer.KryoSerializer;
import lombok.extern.slf4j.Slf4j;


/**
 * 全局序列化器持有者
 *
 * @author zhihao.mao
 */

@Slf4j
public class GlobalSerializerHolder implements SerializerHolder {

    private Serializer serializer;

    private static volatile GlobalSerializerHolder serializerHolder;

    private GlobalSerializerHolder() {

    }

    public static GlobalSerializerHolder getInstance() {
        if (ObjectUtil.isNotEmpty(serializerHolder)) {

            return serializerHolder;
        }

        synchronized (GlobalSerializerHolder.class) {
            if (ObjectUtil.isNotEmpty(serializerHolder)) {

                return serializerHolder;
            }
            serializerHolder = new GlobalSerializerHolder();
        }

        return serializerHolder;
    }

    @Override
    public Serializer get() {

        if (ObjectUtil.isEmpty(serializer)) {
            log.warn("设置序列化器: 由于未主动设置序列化器, 将采用默认序列化器KryoSerializer");

            serializer = KryoSerializer.getInstance();
        }

        return serializer;
    }

    @Override
    public void set(Serializer serializer) {

        this.serializer = serializer;
    }

    public static <T> byte[] serialize(T obj) {
        GlobalSerializerHolder instance = getInstance();

        return instance.serializer.serialize(obj);
    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        GlobalSerializerHolder instance = getInstance();

        return instance.serializer.deserialize(bytes, clazz);
    }

}
