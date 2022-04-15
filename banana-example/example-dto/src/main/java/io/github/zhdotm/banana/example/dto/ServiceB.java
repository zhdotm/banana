package io.github.zhdotm.banana.example.dto;

import io.github.zhdotm.banana.annotation.BananaAsync;
import io.github.zhdotm.banana.annotation.BananaCallback;

import java.io.Serializable;

public interface ServiceB extends Serializable {

    @BananaAsync
//    @BananaCallback(callbackClazz = ServiceC.class, methodName = "callbackMethod001")
    String serviceBMethod001();

}
