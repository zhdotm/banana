package io.github.zhdotm.banana.starter.web.hanlder.biz;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import io.github.zhdotm.banana.common.constant.BootTypeEnum;
import io.github.zhdotm.banana.common.handler.biz.BizInboundHandler;
import io.github.zhdotm.banana.common.protocol.command.RequestCommand;
import io.github.zhdotm.banana.common.protocol.command.ResponseCommand;
import io.netty.channel.ChannelHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * 服务端业务处理器
 *
 * @author zhihao.mao
 */

@Slf4j
@ChannelHandler.Sharable
public class ServerBizInboundHandler extends BizInboundHandler {

    @Override
    public String getName() {
        return "serverBizInboundHandler";
    }

    @Override
    public BootTypeEnum getBootType() {
        return BootTypeEnum.SERVER;
    }

    @Override
    public Integer getSortId() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected ResponseCommand request(RequestCommand requestCommand) {
        String uniqueId = requestCommand.getUniqueId();
        Class<?> clazz = requestCommand.getClazz();
        String methodName = requestCommand.getMethodName();

        ResponseCommand responseCommand = new ResponseCommand();
        responseCommand.setUniqueId(uniqueId);
        responseCommand.setIsAsync(requestCommand.getIsAsync());
        responseCommand.setIsNeedCallback(requestCommand.getIsNeedCallback());
        responseCommand.setCallbackCommand(requestCommand.getCallbackCommand());
        try {
            Class<?>[] parameterTypes = requestCommand.getParameterTypes();
            Object bean = SpringUtil.getBean(clazz);
            Method method = ReflectUtil.getMethod(clazz, methodName, parameterTypes);
            Object result = method.invoke(bean, requestCommand.getParameters());
            Class<?> returnType = method.getReturnType();
            if (returnType.equals(Void.TYPE)) {
                responseCommand.setIsVoid(Boolean.TRUE);
            } else {
                responseCommand.setIsVoid(Boolean.FALSE);
                responseCommand.setResult(result);
            }
        } catch (Exception e) {
            responseCommand.setIsException(Boolean.TRUE);
            responseCommand.setCauseMessage(ExceptionUtil.getRootCauseMessage(e));
        }

        return responseCommand;
    }

    @Override
    protected void response(ResponseCommand responseCommand) {

        log.warn("服务端业务处理器不处理ResponseCommand: {}", responseCommand);
    }

}
