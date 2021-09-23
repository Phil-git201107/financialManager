package com.chiczu.fmoney.clientconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = "com.chiczu.fmoney.feign")
public class ClientConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientConsumerApplication.class,args);
    }
}
