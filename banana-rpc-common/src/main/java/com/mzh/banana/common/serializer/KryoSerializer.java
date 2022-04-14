package com.mzh.banana.common.serializer;

import cn.hutool.core.util.ObjectUtil;
import com.mzh.banana.common.util.KryoUtil;

/**
 * kryo序列化器
 *
 * @author zhihao.mao
 */

public class KryoSerializer implements Serializer {

    private static volatile KryoSerializer serializer;

    private KryoSerializer() {
    }

    public static KryoSerializer getInstance() {
        if (ObjectUtil.isNotEmpty(serializer)) {

            return serializer;
        }
        synchronized (KryoSerializer.class) {
            if (ObjectUtil.isNotEmpty(serializer)) {

                return serializer;
            }

            serializer = new KryoSerializer();
        }

        return serializer;
    }

    @Override
    public <T> byte[] serialize(T obj) {

        return KryoUtil.writeObjectToByteArray(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return KryoUtil.readObjectFromByteArray(bytes, clazz);
    }

}
