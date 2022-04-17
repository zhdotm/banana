package io.github.zhdotm.banana.common.exception;


import lombok.Getter;
import lombok.Setter;

/**
 * 业务异常
 *
 * @author zhihao.mao
 */

public class BananaBizException extends RuntimeException {

    @Getter
    @Setter
    private String uniqueId;

    public BananaBizException(String uniqueId, String message) {
        super("uniqueId[" + uniqueId + "], " + message);
        this.uniqueId = uniqueId;
    }

    public BananaBizException(String message) {
        super(message);
    }

}
