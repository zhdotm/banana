package com.mzh.banana.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 客户端异常
 *
 * @author zhihao.mao
 */

public class BananaRpcClientException extends RuntimeException {

    @Getter
    @Setter
    private Boolean isNeedCloseCtx = Boolean.FALSE;

    public BananaRpcClientException(String message, Boolean isNeedCloseCtx) {
        super(message);
        this.isNeedCloseCtx = isNeedCloseCtx;
    }

    public BananaRpcClientException(String message) {
        super(message);
    }

}
