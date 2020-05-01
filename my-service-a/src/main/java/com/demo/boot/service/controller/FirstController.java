package com.demo.boot.service.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.demo.boot.service.conf.ServiceBConfiguration;
import com.demo.boot.service.conf.ServiceProxy;
import com.demo.boot.service.pojo.BeautifyJson;
import com.demo.boot.service.pojo.ServiceDetails;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RefreshScope
@RestController
@RibbonClient(name = "my-service-b", configuration = ServiceBConfiguration.class)
public class FirstController {
	
	@Value("${env:default}")
	private String configEnv;
	
	@Value("${app:spring-boot}")
	private String configApp;
	
	@Value("${key:default-key}")
	private String vaultKey;
	
	@Value("${value:default-value}")
	private String vaultValue;
	
	@Autowired
	Environment environment;
	
	@LoadBalanced
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	ServiceProxy serviceProxy;
	
	@GetMapping("/health") 
	public String getHealthStatus() {
		
		//Map<String, String> uriParams = new HashMap<String, String>();
		//uriParams.put("X-Vault-Token", "s.1S5JQl29S5wwHhdXMuaXx5ke");
		
		//ResponseEntity<String> response = restTemplate.getForEntity("http://127.0.0.1:8200/v1/kv/secrets/demo/kv", 
		//		String.class, uriParams);
		
		return "I am alright, don't worry. Says Service A with following details: Env: "+this.configEnv
				+" App: "+this.configApp + "Secrets from Vault: key:"+vaultKey + " value:"+vaultValue;
	}
	
	@HystrixCommand(commandKey = "fetch-b", fallbackMethod="fetchDefaultServiceDetails")
	@GetMapping("/fetch-bc")
	public BeautifyJson fetchServiceDetails() throws UnknownHostException {
		
		log.info("Calling Service B...");
		
		ServiceDetails serviceDetailsForA = new ServiceDetails();
		
		String appName = environment.getProperty("spring.application.name");
		String port = environment.getProperty("local.server.port");
		String appUrl = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port;
		
		serviceDetailsForA.setServiceName(appName);
		serviceDetailsForA.setServicePort(port);
		serviceDetailsForA.setServiceUrl(appUrl);
		
		List<ServiceDetails> listOfServices = new ArrayList<ServiceDetails>();
		listOfServices.add(serviceDetailsForA);
		
		BeautifyJson jsonResponse = new BeautifyJson();
		
		String url = "http://my-service-b/fetch-b";
		log.debug("Calling Service B from URL: {}", url);
		
		//BeautifyJson responseFromDownstream = restTemplate.getForObject(url, BeautifyJson.class);
		
		// Instead of Rest Template Feign will take care of rest API call
		BeautifyJson responseFromDownstream = serviceProxy.fetchServiceDetails();
		
		List<ServiceDetails> downstreamList = responseFromDownstream.getListOfServices();
			
		if(!downstreamList.isEmpty()) {
			for (ServiceDetails serviceDetails : downstreamList) {
				listOfServices.add(serviceDetails);
			}
			
		}
		
		jsonResponse.setListOfServices(listOfServices);
		
		return jsonResponse;
		
	}
	
	public BeautifyJson fetchDefaultServiceDetails() throws UnknownHostException {
		
		log.info("Calling Service B Failed, enabling Hystrix...");
		
		ServiceDetails serviceDetailsForA = new ServiceDetails();
		
		String appName = environment.getProperty("spring.application.name");
		String port = environment.getProperty("local.server.port");
		String appUrl = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port;
		
		serviceDetailsForA.setServiceName(appName);
		serviceDetailsForA.setServicePort(port);
		serviceDetailsForA.setServiceUrl(appUrl);
		
		ServiceDetails serviceDetailsForB = new ServiceDetails();
		serviceDetailsForB.setServiceName("No App Found");
		serviceDetailsForB.setServicePort("No Port Found");
		serviceDetailsForB.setServiceUrl("No URL Found");
		
		List<ServiceDetails> listOfServices = new ArrayList<ServiceDetails>();
		listOfServices.add(serviceDetailsForA);
		listOfServices.add(serviceDetailsForB);
		
		BeautifyJson jsonResponse = new BeautifyJson();
		
		jsonResponse.setListOfServices(listOfServices);
		
		return jsonResponse;
	}
	
}
