package io.github.zhdotm.banana.common.protocol.command;

import lombok.Data;

import java.io.Serializable;

/**
 * 调用命令
 *
 * @author zhihao.mao
 */

@Data
public class RequestCommand implements Command, Serializable {

    /**
     * 命令类型ID
     */
    private String commandId = "REQ";

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
     * 类名
     */
    private String clazzName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 参数类型列表
     */
    private Class<?>[] parameterTypes;

    /**
     * 参数列表
     */
    private Object[] parameters;


    /**
     * 回调命令
     */
    private CallbackCommand callbackCommand;
}
