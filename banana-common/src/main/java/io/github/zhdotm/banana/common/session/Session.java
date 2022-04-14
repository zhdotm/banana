package io.github.zhdotm.banana.common.session;

import io.github.zhdotm.banana.common.protocol.command.Command;
import io.github.zhdotm.banana.common.protocol.BasicMessage;

/**
 * 会话
 *
 * @author zhihao.mao
 */

public interface Session {

    /**
     * 获取会话ID
     *
     * @return 会话ID
     */
    String getSessionId();

    /**
     * 关闭会话
     */
    void close();


    /**
     * 发送命令
     *
     * @param command 命令
     * @param <T>     类型
     */
    <T extends Command> void sendCommand(T command);

    /**
     * 失败
     *
     * @param uniqueId 唯一ID
     * @param info     信息
     */
    void sendFail(String uniqueId, String info);

    /**
     * 异常
     *
     * @param uniqueId 唯一ID
     * @param info     信息
     */
    void sendException(String uniqueId, String info);

    /**
     * 发送消息
     *
     * @param message 消息
     */
    void sendMessage(BasicMessage.Message message);
}
