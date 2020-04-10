package com.demo.boot.support;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

import com.demo.boot.support.filter.ErrorFilter;
import com.demo.boot.support.filter.PostFilter;
import com.demo.boot.support.filter.PreFilter;
import com.demo.boot.support.filter.RouteFilter;

@EnableZuulProxy
@EnableEurekaClient
@SpringBootApplication
public class MyZuulGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyZuulGatewayApplication.class, args);
	}
	
	@Bean
    public PreFilter preFilter() {
        return new PreFilter();
    }
    @Bean
    public PostFilter postFilter() {
        return new PostFilter();
    }
    @Bean
    public ErrorFilter errorFilter() {
        return new ErrorFilter();
    }
    @Bean
    public RouteFilter routeFilter() {
        return new RouteFilter();
    }

}

