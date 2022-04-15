package io.github.zhdotm.banana.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhihao.mao
 */

@AllArgsConstructor
public enum SessionEnum {

    /**
     * sessionId前缀
     */
    ID_PREFIX("sessionId:"),
    ;

    @Getter
    private final String value;

    public static String createSessionId(String clientName, String serverName, String serverUrl) {

        return ID_PREFIX.getValue() + clientName + ":" + serverName + ":" + serverUrl;
    }

}
