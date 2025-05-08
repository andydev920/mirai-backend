package jp.mirai.module.infra.framework.file.config;

import jp.mirai.module.infra.framework.file.core.client.FileClientFactory;
import jp.mirai.module.infra.framework.file.core.client.FileClientFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件配置类
 *
 * @author mirai
 */
@Configuration(proxyBeanMethods = false)
public class MiraiFileAutoConfiguration {

    @Bean
    public FileClientFactory fileClientFactory() {
        return new FileClientFactoryImpl();
    }

}
