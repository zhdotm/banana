package io.github.zhdotm.banana.starter.web.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import io.github.zhdotm.banana.common.exception.BananaBizException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 响应工具
 *
 * @author zhihao.mao
 */

@Slf4j
public class ResponseUtil {

    /**
     * 锁缓存
     */
    private static final ConcurrentHashMap<String, CountDownLatch> LOCK_CACHE = MapUtil.newConcurrentHashMap();

    /**
     * 响应缓存
     */
    private static final ConcurrentHashMap<String, Object> RESPONSE_CACHE = MapUtil.newConcurrentHashMap();

    /**
     * 无响应缓存
     */
    private static final ConcurrentHashMap<String, Object> VOID_CACHE = MapUtil.newConcurrentHashMap();

    /**
     * 异常响应缓存
     */
    private static final ConcurrentHashMap<String, Exception> EXCEPTION_CACHE = MapUtil.newConcurrentHashMap();

    /**
     * 设置响应
     *
     * @param uniqueId 唯一ID
     * @param time     时间
     * @param unit     单位
     * @param <T>      类型
     * @return 出参
     */
    @SneakyThrows
    public static <T> T getResponse(String uniqueId, Long time, TimeUnit unit) {
        tryLock(uniqueId, time, unit);
        T result = (T) RESPONSE_CACHE.remove(uniqueId);
        throwExceptionIfExist(uniqueId);
        
        return result;
    }

    /**
     * 获取无
     *
     * @param uniqueId 唯一ID
     * @param time     时间
     * @param unit     单位
     */
    public static void getVoid(String uniqueId, Long time, TimeUnit unit) {
        tryLock(uniqueId, time, unit);

        VOID_CACHE.remove(uniqueId);
        throwExceptionIfExist(uniqueId);
    }

    @SneakyThrows
    public static void throwExceptionIfExist(String uniqueId) {
        Exception exception = getException(uniqueId);
        if (ObjectUtil.isNotEmpty(exception)) {
            throw exception;
        }
    }

    public static Exception getException(String uniqueId) {

        return EXCEPTION_CACHE.get(uniqueId);
    }

    /**
     * 设置响应
     *
     * @param uniqueId 唯一ID
     * @param obj      响应
     */
    public static void setResponse(String uniqueId, Object obj) {
        RESPONSE_CACHE.put(uniqueId, obj);
        unlock(uniqueId);
    }

    /**
     * 设置无响应
     *
     * @param uniqueId 唯一ID
     */
    public static void setVoid(String uniqueId) {
        VOID_CACHE.put(uniqueId, new Object());
        unlock(uniqueId);
    }

    /**
     * 设置异常响应
     *
     * @param causeMessage 异常信息
     */
    public static void setException(String uniqueId, String causeMessage) {
        EXCEPTION_CACHE.put(uniqueId, new BananaBizException(uniqueId, causeMessage));
        unlock(uniqueId);
    }

    /**
     * 上锁
     *
     * @param uniqueId 唯一ID
     * @param time     时间
     * @param unit     单位
     */
    @SneakyThrows
    public static void tryLock(String uniqueId, Long time, TimeUnit unit) {
        CountDownLatch lock = LOCK_CACHE.getOrDefault(uniqueId, new CountDownLatch(1));
        LOCK_CACHE.put(uniqueId, lock);

        if (!lock.await(time, unit)) {
            LOCK_CACHE.remove(uniqueId);
            throw new BananaBizException(uniqueId, "等待超时: " + time + unit + "");
        }
    }

    /**
     * 解锁
     *
     * @param uniqueId 唯一ID
     */
    public static void unlock(String uniqueId) {
        CountDownLatch lock = LOCK_CACHE.get(uniqueId);

        if (ObjectUtil.isEmpty(lock)) {
            throw new BananaBizException(uniqueId, "解锁失败: 锁不存在");
        }

        lock.countDown();
        LOCK_CACHE.remove(uniqueId);
    }

}
