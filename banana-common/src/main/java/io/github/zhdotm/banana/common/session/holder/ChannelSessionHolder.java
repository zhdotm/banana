package io.github.zhdotm.banana.common.session.holder;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.github.zhdotm.banana.common.session.ChannelSession;
import io.github.zhdotm.banana.common.session.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
        if (StrUtil.isBlank(sessionId)) {

            return Boolean.TRUE;
        }
        SESSION_CACHE.remove(sessionId);

        log.info("移除会话缓存成功: {}", sessionId);
        return Boolean.TRUE;
    }

    @Override
    public Session getSession(String sessionId) {

        return SESSION_CACHE.get(sessionId);
    }

}
