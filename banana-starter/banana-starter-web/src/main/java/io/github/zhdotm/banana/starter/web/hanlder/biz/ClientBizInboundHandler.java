package io.github.zhdotm.banana.starter.web.hanlder.biz;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.github.zhdotm.banana.common.constant.BootTypeEnum;
import io.github.zhdotm.banana.common.handler.biz.BizInboundHandler;
import io.github.zhdotm.banana.common.protocol.command.CallbackCommand;
import io.github.zhdotm.banana.common.protocol.command.RequestCommand;
import io.github.zhdotm.banana.common.protocol.command.ResponseCommand;
import io.github.zhdotm.banana.starter.web.util.ResponseUtil;
import io.netty.channel.ChannelHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * 客户端业务处理器
 *
 * @author zhihao.mao
 */

@Slf4j
@ChannelHandler.Sharable
public class ClientBizInboundHandler extends BizInboundHandler {

    @Override
    public String getName() {

        return "clientBizInboundHandler";
    }

    @Override
    public BootTypeEnum getBootType() {

        return BootTypeEnum.CLIENT;
    }

    @Override
    public Integer getSortId() {

        return Integer.MAX_VALUE;
    }

    @Override
    protected ResponseCommand request(RequestCommand requestCommand) {
        log.warn("客户端业务处理器不处理RequestCommand: {}", requestCommand);

        return null;
    }

    @SneakyThrows
    @Override
    protected void response(ResponseCommand responseCommand) {
        String uniqueId = responseCommand.getUniqueId();
        Boolean isVoid = responseCommand.getIsVoid();
        Object result = responseCommand.getResult();

        if (responseCommand.getIsNeedCallback()) {
            CallbackCommand callbackCommand = responseCommand.getCallbackCommand();
            Class<?> clazz = callbackCommand.getClazz();
            String methodName = callbackCommand.getMethodName();
            Class<?> parameterClazz = callbackCommand.getParameterClazz();
            Object bean = SpringUtil.getBean(clazz);

            Method method;
            if (parameterClazz.equals(Void.TYPE)) {
                method = bean.getClass().getMethod(methodName);
                method.invoke(bean);
            } else {
                method = bean.getClass().getMethod(methodName, parameterClazz);
                method.invoke(bean, result);
            }
        }

        if (responseCommand.getIsAsync()) {
            return;
        }

        if (isVoid) {
            ResponseUtil.setVoid(uniqueId);
            return;
        }
        ResponseUtil.setResponse(uniqueId, result);
    }

}
