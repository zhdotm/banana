package io.github.zhdotm.banana.example.a.service.impl;

import io.github.zhdotm.banana.example.dto.ParameterBDto;
import io.github.zhdotm.banana.example.service.ServiceC;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServiceCImpl implements ServiceC {


    @Override
    public void callbackMethod001(ParameterBDto parameterBDto) {
        log.info("有返回值回调callbackMethod001: {}", parameterBDto);
    }

    @Override
    public void callbackMethod002() {
        log.info("有返回值回调callbackMethod002");
    }
}
