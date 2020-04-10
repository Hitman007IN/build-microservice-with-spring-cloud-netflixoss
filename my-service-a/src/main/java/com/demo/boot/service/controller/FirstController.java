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

import com.demo.boot.service.conf.ServiceBConfiguration;
import com.demo.boot.service.pojo.BeautifyJson;
import com.demo.boot.service.pojo.ServiceDetails;

@RestController
@RibbonClient(name = "my-service-b", configuration = ServiceBConfiguration.class)
public class FirstController {

	private static final Logger LOG = LoggerFactory.getLogger(FirstController.class);
	
	@Autowired
	Environment environment;
	
	@LoadBalanced
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Autowired
	RestTemplate restTemplate;
	
	@GetMapping("/health") 
	public String getHealthStatus() {
		return "I am alright, don't worry. Says Service A";
	}
	
	@GetMapping("/fetch-bc")
	public BeautifyJson fetchServiceDetails() throws UnknownHostException {
		
		LOG.info("Calling Service B...");
		
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
		LOG.debug("Calling Service B from URL: {}", url);
		
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
	
}
