package com.example.account.modules.core.config;

import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Kernel JWTs list every permission the account has, per organization — an
 * account belonging to many orgs (e.g. an OWNER across several test orgs)
 * produces a Bearer token well past Reactor Netty's default 8KB max header
 * size, which otherwise gets the request rejected with 431 before it ever
 * reaches a controller.
 */
@Configuration
public class NettyServerConfig {

    @Bean
    public WebServerFactoryCustomizer<NettyReactiveWebServerFactory> maxHeaderSizeCustomizer() {
        return factory -> factory.addServerCustomizers(httpServer ->
                httpServer.httpRequestDecoder(spec -> spec.maxHeaderSize(65536)));
    }
}
