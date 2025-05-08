package jp.mirai.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * start run
 *
 * @author mirai
 */
@SuppressWarnings("SpringComponentScan") // 忽略 IDEA 无法识别 ${mirai.info.base-package}
@SpringBootApplication(scanBasePackages = {"${mirai.info.base-package}.server", "${mirai.info.base-package}.module"})
public class MiraiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiraiServerApplication.class, args);
//        new SpringApplicationBuilder(MiraiServerApplication.class)
//                .applicationStartup(new BufferingApplicationStartup(20480))
//                .run(args);

    }

}
