package com.demo.boot.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class MyServiceCApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyServiceCApplication.class, args);
	}

}
