package com.demo.mytv.config;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.test.context.support.TestPropertySourceUtils;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.ServerSocket;

public class AnotherTestRedisConfig {
    public static class RedisInitializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext context) {
            int port = findAvailablePort();

            RedisServer redisServer;

            try {
                redisServer = new RedisServer(port);
                redisServer.start();
            } catch (IOException e) {
                throw new IllegalStateException("Embedded Redis 서버 실행에 실패했습니다.", e);
            }

            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    context,
                    "spring.data.redis.host=127.0.0.1",
                    "spring.data.redis.port=" + port
            );

            context.addApplicationListener(event -> {
                if (event instanceof ContextClosedEvent) {
                    try {
                        if (redisServer.isActive()) {
                            redisServer.stop();
                        }
                    } catch (IOException e) {
                        throw new IllegalStateException("Embedded Redis 서버 종료에 실패했습니다.", e);
                    }
                }
            });
        }

        private int findAvailablePort() {
            try (ServerSocket socket = new ServerSocket(0)) {
                return socket.getLocalPort();
            } catch (IOException e) {
                throw new IllegalStateException(
                        "Redis 포트 할당 실패", e
                );
            }
        }
    }
}
