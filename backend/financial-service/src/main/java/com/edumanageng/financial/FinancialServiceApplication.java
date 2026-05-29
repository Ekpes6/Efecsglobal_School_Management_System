package com.edumanageng.financial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FinancialServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FinancialServiceApplication.class, args);
    }
}
