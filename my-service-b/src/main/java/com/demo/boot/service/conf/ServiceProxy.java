package com.demo.boot.service.conf;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.demo.boot.service.pojo.BeautifyJson;

//We can have Feign configured as properties file or in a separate class as beans
@FeignClient(name="my-service-c") //, configuration=FeignConfiguration.class)
public interface ServiceProxy {

	@GetMapping("/fetch-c")
	public BeautifyJson fetchServiceDetails();
	
}
