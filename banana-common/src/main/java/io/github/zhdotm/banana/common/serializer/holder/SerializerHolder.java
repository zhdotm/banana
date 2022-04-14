package io.github.zhdotm.banana.common.serializer.holder;

import io.github.zhdotm.banana.common.serializer.Serializer;

/**
 * 序列化器持有者
 *
 * @author zhihao.mao
 */

public interface SerializerHolder {

    /**
     * 获取序列化器
     *
     * @return 序列化器
     */
    Serializer get();

    /**
     * 设置序列化器
     *
     * @param serializer 序列化器
     */
    void set(Serializer serializer);

}
