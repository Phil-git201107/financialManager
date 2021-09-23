package com.chiczu.fmoney.stockprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan(basePackages = {"com.chiczu.fmoney.stockprovider",
                "com.chiczu.fmoney.utils"})
public class StockProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(StockProviderApplication.class,args);
    }
}
