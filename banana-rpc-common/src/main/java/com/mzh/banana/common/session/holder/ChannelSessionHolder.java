package com.mzh.banana.common.session.holder;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.mzh.banana.common.session.ChannelSession;
import com.mzh.banana.common.session.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 通道会话持有者
 *
 * @author zhihao.mao
 */

@Slf4j
public class ChannelSessionHolder implements SessionHolder {

    private static final ConcurrentHashMap<String, ChannelSession> SESSION_CACHE = MapUtil.newConcurrentHashMap();

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
