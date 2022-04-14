package com.mzh.banana.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 服务端异常
 *
 * @author zhihao.mao
 */

public class BananaRpcServerException extends RuntimeException {

    @Getter
    @Setter
    private Boolean isNeedCloseCtx = Boolean.FALSE;

    @Getter
    @Setter
    private Boolean isNeedWriteBackExceptionMessage = Boolean.FALSE;

    @Getter
    @Setter
    private String uniqueId;

    public BananaRpcServerException(String uniqueId, Boolean isNeedWriteBackExceptionMessage, Boolean isNeedCloseCtx, String message) {
        super(message);
        this.isNeedCloseCtx = isNeedCloseCtx;
        this.isNeedWriteBackExceptionMessage = isNeedWriteBackExceptionMessage;
        this.uniqueId = uniqueId;
    }

    public BananaRpcServerException(String uniqueId, Boolean isNeedCloseCtx, String message) {
        super(message);
        this.isNeedCloseCtx = isNeedCloseCtx;
        this.uniqueId = uniqueId;
    }

    public BananaRpcServerException(String message, Boolean isNeedCloseCtx) {
        super(message);
        this.isNeedCloseCtx = isNeedCloseCtx;
    }

    public BananaRpcServerException(String message) {
        super(message);
    }

}
