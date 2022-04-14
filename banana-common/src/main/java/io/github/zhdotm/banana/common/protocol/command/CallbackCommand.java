package io.github.zhdotm.banana.common.protocol.command;

import lombok.Data;

import java.io.Serializable;

/**
 * 回调命令（回调方法的参数固定，(String, String, Object)）
 *
 * @author zhihao.mao
 */

@Data
public class CallbackCommand implements Command, Serializable {

    /**
     * 唯一ID
     */
    private String uniqueId;

    /**
     * 命令类型ID
     */
    private String commandId = "CALL_BACK";

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
    private Class<?>[] parameterTypes = new Class[]{String.class, String.class, Object.class};

}
