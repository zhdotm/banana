package io.github.zhdotm.banana.common.exception;


import lombok.Getter;
import lombok.Setter;

/**
 * 异常
 *
 * @author zhihao.mao
 */

public class BananaCloseException extends RuntimeException {

    @Getter
    @Setter
    private String uniqueId;

    public BananaCloseException(String uniqueId, String message) {
        super("uniqueId[" + uniqueId + "], " + message);
        this.uniqueId = uniqueId;
    }

    public BananaCloseException(String message) {
        super(message);
    }

}
