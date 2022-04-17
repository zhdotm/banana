package io.github.zhdotm.banana.example.a.service.impl;

import io.github.zhdotm.banana.example.dto.ParameterADto;
import io.github.zhdotm.banana.example.dto.ParameterBDto;
import io.github.zhdotm.banana.example.service.ServiceB;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServiceBImpl implements ServiceB {


    @Override
    public ParameterBDto serviceBMethod001(ParameterADto parameterADto) {
        log.info("有入参有返回值测试: {}", parameterADto);

        return new ParameterBDto("有入参有返回值测试xxxx", 1, Boolean.TRUE);
    }

    @Override
    public void serviceBMethod002(ParameterADto parameterADto) {
        log.info("有入参无返回值测试: {}", parameterADto);
    }

    @Override
    public ParameterBDto serviceBMethod003() {
        ParameterBDto parameterBDto = new ParameterBDto("有入参有返回值测试xxxx", 3, Boolean.TRUE);
        log.info("无入参有返回值测试: {}", parameterBDto);
        return parameterBDto;
    }

    @Override
    public void serviceBMethod004() {
        log.info("无入参无返回值测试: {}", Boolean.TRUE);
    }

    @Override
    public ParameterBDto serviceBMethod005() {
        ParameterBDto parameterBDto = new ParameterBDto("有入参有返回值测试xxxx", 5, Boolean.TRUE);
        log.info("有返回值有回调: {}", parameterBDto);
        return parameterBDto;
    }

    @Override
    public void serviceBMethod006() {
        log.info("无返回值有回调");
    }

    @Override
    public ParameterBDto serviceBMethod007(ParameterADto parameterADto) {
        ParameterBDto parameterBDto = new ParameterBDto("有入参有返回值测试xxxx", 7, Boolean.TRUE);
        log.info("异步有入参有返回值测试: {}", parameterADto);
        return parameterBDto;
    }

    @SneakyThrows
    @Override
    public void serviceBMethod008() {
        log.info("抛出异常");
        throw new Exception("serviceBMethod008p抛出的异常");
    }
}
