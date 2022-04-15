package io.github.zhdotm.banana.common.protocol.command;

import lombok.Data;

import java.io.Serializable;

/**
 * 响应命令
 *
 * @author zhihao.mao
 */

@Data
public class ResponseCommand implements Command, Serializable {

    /**
     * 命令类型ID
     */
    private String commandId = "RESP";

    /**
     * 唯一ID
     */
    private String uniqueId;

    /**
     * 是否需要回调
     */
    private Boolean isNeedCallback = Boolean.FALSE;

    /**
     * 是否是异步
     */
    private Boolean isAsync = Boolean.FALSE;

    /**
     * 是否无返回值
     */
    private Boolean isVoid;

    /**
     * 结果
     */
    private Object result;

    /**
     * 回调命令
     */
    private CallbackCommand callbackCommand;

}
