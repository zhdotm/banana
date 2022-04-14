package com.mzh.banana.common.protocol.command;

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
     * 是否无返回值
     */
    private Boolean isVoid;

    /**
     * 状态
     */
    private String code;

    /**
     * 消息
     */
    private String message;

    /**
     * 结果
     */
    private Object result;

    /**
     * 回调命令
     */
    private CallbackCommand callbackCommand;

}
