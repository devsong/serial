package com.gzs.learn.serial;

import java.util.concurrent.CountDownLatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@ImportResource("classpath:/META-INF/applicationContext.xml")
@Slf4j
public class SerialDistributeServer {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(SerialDistributeServer.class, args);
        log.info("----------->Serial Distribute Server Start Success<----------");
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }
}
