package io.github.zhdotm.banana.common.session.holder;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import io.github.zhdotm.banana.common.exception.BananaClientException;
import io.github.zhdotm.banana.common.session.ChannelSession;
import io.github.zhdotm.banana.common.session.Session;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 通道会话持有者
 *
 * @author zhihao.mao
 */

@Slf4j
public class ChannelSessionHolder implements SessionHolder {

    private static final ConcurrentHashMap<String, ChannelSession> SESSION_CACHE = MapUtil.newConcurrentHashMap();

    private static final ConcurrentHashMap<String, CountDownLatch> LOCK_CACHE = MapUtil.newConcurrentHashMap();

    private static volatile ChannelSessionHolder instance;

    private ChannelSessionHolder() {
    }

    /**
     * 获取实例
     *
     * @return 实例
     */
    public static ChannelSessionHolder getInstance() {

        if (ObjectUtil.isNotEmpty(instance)) {

            return instance;
        }

        synchronized (ChannelSessionHolder.class) {
            if (ObjectUtil.isNotEmpty(instance)) {

                return instance;
            }

            instance = new ChannelSessionHolder();
        }

        return instance;
    }

    /**
     * 根据前缀匹配会话
     *
     * @param prefix 前缀
     * @return 会话列表
     */
    public static List<Session> match(String prefix) {
        List<String> sessionIdList = SESSION_CACHE
                .keySet()
                .stream()
                .filter(sessionId -> sessionId.startsWith(prefix))
                .collect(Collectors.toList());

        return SESSION_CACHE
                .values()
                .stream()
                .filter(channelSession -> sessionIdList.contains(channelSession.getSessionId()))
                .collect(Collectors.toList());
    }

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
            throw new BananaClientException("等待超时sessionId[" + sessionId + "]: " + time + unit + "");
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
            throw new BananaClientException("解锁失败sessionId[" + sessionId + "]: 锁不存在");
        }

        lock.countDown();
        LOCK_CACHE.remove(sessionId);
    }

    @Override
    public Boolean addSession(Session session) {
        String sessionId = session.getSessionId();

        if (!(session instanceof ChannelSession)) {
            log.warn("添加会话缓存失败: sessionId[{}]会话非ChannelSession", sessionId);

            return Boolean.FALSE;
        }

        ChannelSession channelSession = (ChannelSession) session;

        if (SESSION_CACHE.containsKey(sessionId)) {
            log.warn("添加会话缓存失败: sessionId[{}]的会话已经存在", sessionId);

            return Boolean.FALSE;
        }

        SESSION_CACHE.put(sessionId, channelSession);

        log.info("添加会话缓存成功: {}", sessionId);
        return Boolean.TRUE;
    }

    @Override
    public Boolean removeSession(String sessionId) {
        SESSION_CACHE.remove(sessionId);

        log.info("移除会话缓存成功: {}", sessionId);
        return Boolean.TRUE;
    }

    @Override
    public Session getSession(String sessionId) {

        return SESSION_CACHE.get(sessionId);
    }

}
