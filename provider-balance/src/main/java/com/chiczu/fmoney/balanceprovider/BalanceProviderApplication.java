package com.chiczu.fmoney.balanceprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableEurekaClient
@EnableAsync
@ComponentScan(basePackages = {"com.chiczu.fmoney.balanceprovider",
        "com.chiczu.fmoney.utils"})
public class BalanceProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(BalanceProviderApplication.class,args);
    }
}
