package io.github.zhdotm.banana.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 客户端异常
 *
 * @author zhihao.mao
 */

public class BananaClientException extends BananaException {

    @Getter
    @Setter
    private Boolean isNeedCloseCtx = Boolean.FALSE;

    public BananaClientException(String message, Boolean isNeedCloseCtx) {
        super(message);
        this.isNeedCloseCtx = isNeedCloseCtx;
    }

    public BananaClientException(String message) {
        super(message);
    }

}
