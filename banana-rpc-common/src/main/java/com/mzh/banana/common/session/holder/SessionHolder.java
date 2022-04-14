package com.mzh.banana.common.session.holder;

import com.mzh.banana.common.session.Session;

/**
 * 会话持有者
 *
 * @author zhihao.mao
 */

public interface SessionHolder {

    /**
     * 添加会话
     *
     * @param session 会话
     */
    Boolean addSession(Session session);

    /**
     * 移除会话
     *
     * @param sessionId 会话ID
     */
    Boolean removeSession(String sessionId);

    /**
     * 获取会话
     *
     * @param sessionId 会话ID
     * @return 会话
     */
    Session getSession(String sessionId);
}
