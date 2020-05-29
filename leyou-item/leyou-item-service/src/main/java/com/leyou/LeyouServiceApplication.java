package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author Jun
 * @create 2020/5/19 - 15:17
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.leyou.item.mapper") //mapper接口的扫描
public class LeyouServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeyouServiceApplication.class);
    }
}
