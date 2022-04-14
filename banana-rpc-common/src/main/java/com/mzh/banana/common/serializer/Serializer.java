package com.mzh.banana.common.serializer;

/**
 * 序列化器
 *
 * @author zhihao.mao
 */

public interface Serializer {

    /**
     * 序列化
     *
     * @param obj 对象
     * @param <T> 类型
     * @return 序列化后的字节数组
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化
     *
     * @param bytes 序列化后数据
     * @param clazz 类型
     * @param <T>   类型
     * @return 反序列化后得到的对象
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);

}
