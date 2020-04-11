package com.demo.boot.service.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.demo.boot.service.conf.ServiceCConfiguration;
import com.demo.boot.service.pojo.BeautifyJson;
import com.demo.boot.service.pojo.ServiceDetails;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RibbonClient(name = "my-service-c", configuration = ServiceCConfiguration.class)
public class MiddleController {

	private static final Logger LOG = LoggerFactory.getLogger(MiddleController.class);
	
	@LoadBalanced
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	Environment environment;
	
	@GetMapping("/health") 
	public String getHealthStatus() {
		return "I am alright, don't worry. Says Service B";
	}
	
	@HystrixCommand(commandKey = "fetch-c", fallbackMethod="fetchDefaultServiceDetails")
	@GetMapping("/fetch-b")
	public BeautifyJson fetchServiceDetails() throws UnknownHostException {
		
		LOG.info("Call Reached Service B...");
		
		ServiceDetails serviceDetailsForB = new ServiceDetails();
		
		String appName = environment.getProperty("spring.application.name");
		String port = environment.getProperty("local.server.port");
		String appUrl = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port;
		
		serviceDetailsForB.setServiceName(appName);
		serviceDetailsForB.setServicePort(port);
		serviceDetailsForB.setServiceUrl(appUrl);
		
		List<ServiceDetails> listOfServices = new ArrayList<ServiceDetails>();
		listOfServices.add(serviceDetailsForB);
		
		String url = "http://my-service-c/fetch-c";
		LOG.debug("Calling Service C from URL: {}", url);
		
		BeautifyJson jsonResponse = new BeautifyJson();
		
		BeautifyJson responseFromDownstream = restTemplate.getForObject(url, BeautifyJson.class);

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
		
		LOG.info("Calling Service C Failed, enabling Hystrix...");
		
		ServiceDetails serviceDetailsForB = new ServiceDetails();
		
		String appName = environment.getProperty("spring.application.name");
		String port = environment.getProperty("local.server.port");
		String appUrl = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port;
		
		serviceDetailsForB.setServiceName(appName);
		serviceDetailsForB.setServicePort(port);
		serviceDetailsForB.setServiceUrl(appUrl);
		
		ServiceDetails serviceDetailsForC = new ServiceDetails();
		serviceDetailsForC.setServiceName("No App Found");
		serviceDetailsForC.setServicePort("No Port Found");
		serviceDetailsForC.setServiceUrl("No URL Found");
		
		List<ServiceDetails> listOfServices = new ArrayList<ServiceDetails>();
		listOfServices.add(serviceDetailsForB);
		listOfServices.add(serviceDetailsForC);
		
		BeautifyJson jsonResponse = new BeautifyJson();
		
		jsonResponse.setListOfServices(listOfServices);
		
		return jsonResponse;
	}
}
