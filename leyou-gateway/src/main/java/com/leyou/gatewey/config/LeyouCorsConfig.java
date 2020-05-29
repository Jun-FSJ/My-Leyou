package com.leyou.gatewey.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author Jun
 * @create 2020/5/22 - 17:16
 */
@Configuration
//处理cors跨域的问题
public class LeyouCorsConfig {

    @Bean
    public CorsFilter corsFilter(){
        CorsConfiguration config = new CorsConfiguration();
        //1) 允许的域,不要写*，否则cookie就无法使用了
        config.addAllowedOrigin("http://manage.leyou.com");
        //2) 是否发送Cookie信息
        config.setAllowCredentials(true);
        // 3）允许的头信息
        config.addAllowedHeader("*");
        //4)允许的请求方式
        config.addAllowedMethod("*");
        //添加映射路径，拦截一切请求
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**",config);
        return new CorsFilter(configSource);
    }
}
