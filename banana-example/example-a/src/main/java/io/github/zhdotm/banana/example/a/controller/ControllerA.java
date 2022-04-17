package io.github.zhdotm.banana.example.a.controller;

import io.github.zhdotm.banana.example.dto.ParameterADto;
import io.github.zhdotm.banana.example.dto.ParameterBDto;
import io.github.zhdotm.banana.example.service.ServiceB;
import io.github.zhdotm.banana.annotation.BananaRemoteApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author zhihao.mao
 */

@RestController
public class ControllerA {

    @BananaRemoteApi(serverName = "bApp", serverUrl = "127.0.0.1:8899")
    private ServiceB serviceB;

    /**
     * 有入参有返回值
     *
     * @return 出参
     */
    @GetMapping("/test1")
    public ParameterBDto test1() {
        ParameterADto parameterADto = new ParameterADto("test1", 1, Boolean.TRUE);

        return serviceB.serviceBMethod001(parameterADto);
    }

    /**
     * 有入参无返回值
     */
    @GetMapping("/test2")
    public void test2() {
        ParameterADto parameterADto = new ParameterADto("test1", 1, Boolean.TRUE);

        serviceB.serviceBMethod002(parameterADto);
    }

    /**
     * 无入参有返回值
     *
     * @return 出参
     */
    @GetMapping("/test3")
    public ParameterBDto test3() {

        return serviceB.serviceBMethod003();
    }

    /**
     * 无入参无返回值
     */
    @GetMapping("/test4")
    public void test4() {

        serviceB.serviceBMethod004();
    }

    /**
     * 有返回值有回调
     *
     * @return 出参
     */
    @GetMapping("/test5")
    public ParameterBDto test5() {

        return serviceB.serviceBMethod005();
    }

    /**
     * 无返回值有回调
     */
    @GetMapping("/test6")
    public void test6() {

        serviceB.serviceBMethod006();
    }

    /**
     * 异步
     *
     * @return 出参数
     */
    @GetMapping("/test7")
    public ParameterBDto test7() {
        ParameterADto parameterADto = new ParameterADto("test1", 1, Boolean.TRUE);

        return serviceB.serviceBMethod007(parameterADto);
    }

    /**
     * 异常
     */
    @GetMapping("/test8")
    public void test8() {

        serviceB.serviceBMethod008();
    }

}
