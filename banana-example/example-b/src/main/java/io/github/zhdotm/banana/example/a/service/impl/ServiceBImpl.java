package io.github.zhdotm.banana.example.a.service.impl;

import io.github.zhdotm.banana.example.dto.ServiceB;
import org.springframework.stereotype.Component;

@Component
public class ServiceBImpl implements ServiceB {

    @Override
    public String serviceBMethod001() {
        System.out.println("古古怪怪怪: afadasadacasdadcasdas");

        return "zzzzzzzzzz";
//        return "古古怪怪怪: " + param001;
    }

}
