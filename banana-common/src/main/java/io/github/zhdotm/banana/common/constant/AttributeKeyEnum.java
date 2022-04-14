package io.github.zhdotm.banana.common.constant;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 属性键枚举
 *
 * @author zhihao.mao
 */

@AllArgsConstructor
public enum AttributeKeyEnum {

    /**
     * 属性键枚举
     */
    CLIENT_IP("clientIp"),
    SERVER_IP("serverIp"),
    ACCESS_TOKEN("accessToken"),
    SESSION_ID("sessionId"),
    ;

    @Getter
    private final String value;

    public AttributeKey<String> getAttributeKey() {

        return AttributeKey.valueOf(value);
    }

    public Attribute<String> getAttribute(Channel channel) {

        return channel.attr(getAttributeKey());
    }

    public String getAttributeValue(Channel channel) {

        return getAttribute(channel).get();
    }

}
