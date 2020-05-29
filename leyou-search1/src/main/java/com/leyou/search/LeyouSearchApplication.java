package com.leyou.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Jun
 * @create 2020/5/29 - 8:37
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class LeyouSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeyouSearchApplication.class);
    }
}
