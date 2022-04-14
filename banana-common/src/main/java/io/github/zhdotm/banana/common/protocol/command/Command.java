package io.github.zhdotm.banana.common.protocol.command;

/**
 * 命令
 *
 * @author zhihao.mao
 */

public interface Command {

    /**
     * 获取命令类型ID
     *
     * @return 命令类型ID
     */
    String getCommandId();

    /**
     * 获取唯一ID
     *
     * @return 唯一ID
     */
    String getUniqueId();

}
