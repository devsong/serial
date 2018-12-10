package com.gzs.learn.serial;

import java.util.concurrent.CountDownLatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@ImportResource({"classpath:META-INF/applicationContext.xml"})
@Slf4j
public class SerialManagerServer {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(SerialManagerServer.class, args);
        ctx.registerShutdownHook();
        log.info("----------->Serial Manager Server Start Success<----------");
        final CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }
}
