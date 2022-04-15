package io.github.zhdotm.banana.starter.web.processor;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import io.github.zhdotm.banana.annotation.BananaAsync;
import io.github.zhdotm.banana.common.constant.SessionEnum;
import io.github.zhdotm.banana.common.protocol.command.CallbackCommand;
import io.github.zhdotm.banana.common.protocol.command.RequestCommand;
import io.github.zhdotm.banana.common.session.Session;
import io.github.zhdotm.banana.common.session.holder.ChannelSessionHolder;
import io.github.zhdotm.banana.common.util.UniqueIdUtil;
import io.github.zhdotm.banana.annotation.BananaCallback;
import io.github.zhdotm.banana.annotation.BananaRemoteApi;
import io.github.zhdotm.banana.starter.web.client.BananaWebClient;
import io.github.zhdotm.banana.starter.web.config.properties.BananaWebConfigProperties;
import io.github.zhdotm.banana.starter.web.util.ResponseUtil;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 后置处理器
 *
 * @author zhihao.mao
 */

public class BananaProcessor implements InstantiationAwareBeanPostProcessor {

    @Value("${spring.application.name}")
    private String appName;

    @Autowired
    private BananaWebConfigProperties bananaWebConfigProperties;

    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                BananaRemoteApi bananaRemoteApi = field.getAnnotation(BananaRemoteApi.class);
                if (ObjectUtil.isEmpty(bananaRemoteApi)) {
                    return;
                }
                field.setAccessible(Boolean.TRUE);

                String serverName = bananaRemoteApi.serverName();
                //TODO 根据serverName匹配对应的serverUrl集合，之后负载均衡
                String serverUrl = bananaRemoteApi.serverUrl();
                Long timeout = bananaRemoteApi.timeout();
                Class<?> fieldTypeClazz = field.getType();
                String remoteClazzName = fieldTypeClazz.getName();

                Object fieldValue = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{fieldTypeClazz}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        String sessionId = SessionEnum.createSessionId(appName, serverName, serverUrl);
                        Session session = ChannelSessionHolder.getInstance().getSession(sessionId);
                        if (ObjectUtil.isEmpty(session)) {
                            synchronized (ChannelSessionHolder.class) {
                                if (ObjectUtil.isEmpty(session)) {
                                    BananaWebClient bananaWebClient = new BananaWebClient();
                                    Map<String, Object> map = MapUtil.newHashMap();
                                    map.put("serverName", serverName);
                                    map.put("clientName", appName);
                                    map.put("serverUrl", serverUrl);
                                    String accessToken = bananaWebClient.createAccessToken(map);
                                    String host = serverUrl.split(":")[0];
                                    Integer port = Integer.parseInt(serverUrl.split(":")[1]);
                                    ThreadUtil.execAsync(new Runnable() {
                                        @Override
                                        public void run() {
                                            bananaWebClient.connect(host, port, accessToken, sessionId);
                                        }
                                    });
                                    ChannelSessionHolder.tryLock(sessionId, bananaWebConfigProperties.getCreateSessionTimeout(), TimeUnit.SECONDS);
                                    session = ChannelSessionHolder.getInstance().getSession(sessionId);
                                }
                            }
                        }

                        String uniqueId = UniqueIdUtil.getNextId();
                        RequestCommand requestCommand = new RequestCommand();
                        requestCommand.setUniqueId(uniqueId);
                        requestCommand.setClazzName(remoteClazzName);
                        requestCommand.setMethodName(method.getName());
                        requestCommand.setParameterTypes(method.getParameterTypes());
                        requestCommand.setParameters(args);

                        //回调
                        BananaCallback bananaCallback = method.getDeclaredAnnotation(BananaCallback.class);
                        if (ObjectUtil.isNotEmpty(bananaCallback)) {
                            Class<?> callbackClazz = bananaCallback.callbackClazz();
                            CallbackCommand callbackCommand = new CallbackCommand();
                            callbackCommand.setUniqueId(uniqueId);
                            callbackCommand.setClazz(callbackClazz);
                            callbackCommand.setMethodName(bananaCallback.methodName());
                            callbackCommand.setParameterClazz(method.getReturnType());
                            requestCommand.setIsNeedCallback(Boolean.TRUE);
                            requestCommand.setCallbackCommand(callbackCommand);
                        }

                        //异步
                        Boolean isAsync = ObjectUtil.isNotEmpty(method.getDeclaredAnnotation(BananaAsync.class));
                        requestCommand.setIsAsync(isAsync);

                        session.sendCommand(requestCommand);

                        if (isAsync) {
                            return null;
                        }

                        if (method.getReturnType().equals(Void.TYPE)) {
                            ResponseUtil.getVoid(uniqueId, timeout, TimeUnit.SECONDS);
                            return null;
                        }

                        return ResponseUtil.getResponse(uniqueId, timeout, TimeUnit.SECONDS);
                    }
                });
                field.set(bean, fieldValue);
            }
        });

        return bean;
    }

}
