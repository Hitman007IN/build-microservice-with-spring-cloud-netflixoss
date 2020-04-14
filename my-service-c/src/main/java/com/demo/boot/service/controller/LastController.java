package com.demo.boot.service.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.boot.service.pojo.BeautifyJson;
import com.demo.boot.service.pojo.ServiceDetails;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RefreshScope
@RestController
public class LastController {
	
	@Autowired
	Environment environment;
	
	@Value("${env}")
	private String configEnv;
	
	@Value("${app}")
	private String configApp;
	
	@GetMapping("/health") 
	public String getHealthStatus() {
		return "I am alright, don't worry. Says Service C with following details: Env: "+this.configEnv+" App: "+this.configApp;
	}
	
	@GetMapping("/fetch-c")
	public BeautifyJson fetchServiceDetails() throws UnknownHostException {
		
		log.info("Call Reached Service C...");
		
		ServiceDetails serviceDetailsForC = new ServiceDetails();
		
		String appName = environment.getProperty("spring.application.name");
		String port = environment.getProperty("local.server.port");
		String appUrl = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port;
		
		serviceDetailsForC.setServiceName(appName);
		serviceDetailsForC.setServicePort(port);
		serviceDetailsForC.setServiceUrl(appUrl);
		
		List<ServiceDetails> listOfServices = new ArrayList<ServiceDetails>();
		listOfServices.add(serviceDetailsForC);
		
		BeautifyJson jsonResponse = new BeautifyJson();
		jsonResponse.setListOfServices(listOfServices);
		
		return jsonResponse;
	}
}
