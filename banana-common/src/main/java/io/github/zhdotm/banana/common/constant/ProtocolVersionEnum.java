package io.github.zhdotm.banana.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 版本号枚举
 *
 * @author zhihao.mao
 */

@AllArgsConstructor
public enum ProtocolVersionEnum {

    /**
     * 版本号枚举
     */
    ONE(1),
    ;

    @Getter
    private final int value;

}
