package io.github.zhdotm.banana.example.a.controller;

import io.github.zhdotm.banana.example.dto.ServiceB;
import io.github.zhdotm.banana.annotation.BananaRemoteApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhihao.mao
 */

@RestController
public class ControllerA {

    @BananaRemoteApi(serverName = "bapp",
            serverUrl = "127.0.0.1:8899"
//            ,
//            remoteClazzName = "io.github.zhdotm.banana.example.dto.ServiceB"
    )
    private ServiceB serviceB;

    @GetMapping("/testb")
    public void testb() {

        System.out.println("响应++++++++++: " + serviceB.serviceBMethod001());
    }

}
