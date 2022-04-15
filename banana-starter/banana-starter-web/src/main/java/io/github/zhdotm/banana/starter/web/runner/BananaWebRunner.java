package io.github.zhdotm.banana.starter.web.runner;

import cn.hutool.core.thread.ThreadUtil;
import io.github.zhdotm.banana.starter.web.config.properties.BananaWebConfigProperties;
import io.github.zhdotm.banana.starter.web.server.BananaWebServer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * 运行器
 *
 * @author zhihao.mao
 */

@Slf4j
@AllArgsConstructor
public class BananaWebRunner implements ApplicationRunner {

    private final BananaWebConfigProperties bananaWebConfigProperties;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ThreadUtil.execAsync(new Runnable() {
            @Override
            public void run() {
                Integer port = bananaWebConfigProperties.getPort();
                BananaWebServer bananaWebServer = new BananaWebServer();
                bananaWebServer.setPort(port);
                log.info("启动BananaWebServer: port[{}]", port);
                bananaWebServer.start();
            }
        });
    }

}
