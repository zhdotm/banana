package io.github.zhdotm.banana.example.service;

import io.github.zhdotm.banana.annotation.BananaAsync;
import io.github.zhdotm.banana.annotation.BananaCallback;
import io.github.zhdotm.banana.example.dto.ParameterADto;
import io.github.zhdotm.banana.example.dto.ParameterBDto;
import io.github.zhdotm.banana.example.service.ServiceC;

import java.io.Serializable;

public interface ServiceB extends Serializable {


    /**
     * 有入参有返回值
     *
     * @param parameterADto 入参
     * @return 返回值
     */
    ParameterBDto serviceBMethod001(ParameterADto parameterADto);

    /**
     * 有入参无返回值
     *
     * @param parameterADto 入参
     */
    void serviceBMethod002(ParameterADto parameterADto);

    /**
     * 无入参有返回值
     *
     * @return 返回值
     */
    ParameterBDto serviceBMethod003();

    /**
     * 无入参无返回值
     *
     * @return 返回值
     */
    void serviceBMethod004();

    /**
     * 有返回值有回调
     *
     * @return 返回值
     */
    @BananaCallback(callbackClazz = ServiceC.class, methodName = "callbackMethod001")
    ParameterBDto serviceBMethod005();

    /**
     * 无返回值有回调
     */
    @BananaCallback(callbackClazz = ServiceC.class, methodName = "callbackMethod002")
    void serviceBMethod006();


    /**
     * 异步
     *
     * @param parameterADto 入参
     * @return 返回值
     */
    @BananaAsync
    ParameterBDto serviceBMethod007(ParameterADto parameterADto);

    /**
     * 跑出异常
     */
    void serviceBMethod008();
}
