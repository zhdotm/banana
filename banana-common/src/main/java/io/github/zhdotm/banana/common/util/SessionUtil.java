package io.github.zhdotm.banana.common.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import io.github.zhdotm.banana.common.exception.BananaCloseException;
import lombok.SneakyThrows;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 会话工具
 *
 * @author zhihao.mao
 */
public class SessionUtil {

    private static final ConcurrentHashMap<String, CountDownLatch> LOCK_CACHE = MapUtil.newConcurrentHashMap();

    /**
     * 上锁
     *
     * @param sessionId 会话ID
     * @param time      时间
     * @param unit      单位
     */
    @SneakyThrows
    public static void tryLock(String sessionId, Long time, TimeUnit unit) {
        CountDownLatch lock = LOCK_CACHE.getOrDefault(sessionId, new CountDownLatch(1));
        LOCK_CACHE.put(sessionId, lock);

        if (!lock.await(time, unit)) {
            LOCK_CACHE.remove(sessionId);
            throw new BananaCloseException("等待超时sessionId[" + sessionId + "]: " + time + unit + "");
        }
    }

    /**
     * 解锁
     *
     * @param sessionId 会话ID
     */
    public static void unlock(String sessionId) {
        CountDownLatch lock = LOCK_CACHE.get(sessionId);

        if (ObjectUtil.isEmpty(lock)) {
            throw new BananaCloseException("解锁失败sessionId[" + sessionId + "]: 锁不存在");
        }

        lock.countDown();
        LOCK_CACHE.remove(sessionId);
    }

}
