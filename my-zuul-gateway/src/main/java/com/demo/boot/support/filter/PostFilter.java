package com.demo.boot.support.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.zuul.ZuulFilter;

public class PostFilter extends ZuulFilter {

	private static final Logger LOG = LoggerFactory.getLogger(PostFilter.class);
	
	@Override
	public String filterType() {
		return "post";
	}

	@Override
	public int filterOrder() {
		return 1;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
		
		LOG.info("Inside Response Filter");
		return null;
	}
}
