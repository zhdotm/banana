package io.github.zhdotm.banana.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 服务端异常
 *
 * @author zhihao.mao
 */

public class BananaServerException extends RuntimeException {

    @Getter
    @Setter
    private Boolean isNeedCloseCtx = Boolean.FALSE;

    @Getter
    @Setter
    private Boolean isNeedWriteBackExceptionMessage = Boolean.FALSE;

    @Getter
    @Setter
    private String uniqueId;

    public BananaServerException(String uniqueId, Boolean isNeedWriteBackExceptionMessage, Boolean isNeedCloseCtx, String message) {
        super(message);
        this.isNeedCloseCtx = isNeedCloseCtx;
        this.isNeedWriteBackExceptionMessage = isNeedWriteBackExceptionMessage;
        this.uniqueId = uniqueId;
    }

    public BananaServerException(String uniqueId, Boolean isNeedCloseCtx, String message) {
        super(message);
        this.isNeedCloseCtx = isNeedCloseCtx;
        this.uniqueId = uniqueId;
    }

    public BananaServerException(String message, Boolean isNeedCloseCtx) {
        super(message);
        this.isNeedCloseCtx = isNeedCloseCtx;
    }

    public BananaServerException(String message) {
        super(message);
    }

}
