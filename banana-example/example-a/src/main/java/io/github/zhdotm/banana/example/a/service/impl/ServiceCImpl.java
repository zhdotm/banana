package io.github.zhdotm.banana.example.a.service.impl;

import io.github.zhdotm.banana.example.dto.ServiceC;
import org.springframework.stereotype.Component;

@Component
public class ServiceCImpl implements ServiceC {

    @Override
    public void callbackMethod001(String s) {
        System.out.println("回调请求成功: " + s);
    }

}
