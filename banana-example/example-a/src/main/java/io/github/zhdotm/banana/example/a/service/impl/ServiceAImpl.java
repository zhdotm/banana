package io.github.zhdotm.banana.example.a.service.impl;

import io.github.zhdotm.banana.example.dto.ServiceA;
import org.springframework.stereotype.Component;

@Component
public class ServiceAImpl implements ServiceA {

    @Override
    public String serviceAMethod001(String param001) {
        return "哈赛克: " + param001;
    }

}

