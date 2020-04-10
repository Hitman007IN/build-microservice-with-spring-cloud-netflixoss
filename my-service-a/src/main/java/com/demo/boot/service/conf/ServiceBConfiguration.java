package com.demo.boot.service.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.PingUrl;
import com.netflix.loadbalancer.AvailabilityFilteringRule;

/**
 * The default IPing is a NoOpPing (which doesn’t actually ping server instances, 
 * instead always reporting that they’re stable), 
 * 
 * and the default IRule is a ZoneAvoidanceRule (which avoids the Amazon EC2 zone 
 * that has the most malfunctioning servers, and might thus be a bit difficult 
 * to try out in our local environment)
 * 
 * 
 * Our IPing is a PingUrl, which will ping a URL to check the status of each server. 
 * Ribbon will get an HTTP 200 response when it pings a running server. 
 * 
 * The IRule we set up, the AvailabilityFilteringRule, will use Ribbon’s built-in 
 * circuit breaker functionality to filter out any servers in an “open-circuit” state: 
 * if a ping fails to connect to a given server, or if it gets a read failure for the server, 
 * Ribbon will consider that server “dead” until it begins to respond normally.
 * 
 * */
public class ServiceBConfiguration {

	@Autowired
	IClientConfig ribbonClientConfig;

	@Bean
	public IPing ribbonPing(IClientConfig config) {
		return new PingUrl();
	}

	@Bean
	public IRule ribbonRule(IClientConfig config) {
		return new AvailabilityFilteringRule();
	}
}
