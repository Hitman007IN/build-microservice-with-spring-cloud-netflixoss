package com.demo.boot.service.pojo;

public class ServiceDetails {

	private String serviceName;
	private String servicePort;
	private String serviceUrl;
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServicePort() {
		return servicePort;
	}
	public void setServicePort(String servicePort) {
		this.servicePort = servicePort;
	}
	public String getServiceUrl() {
		return serviceUrl;
	}
	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
	
	@Override
	public String toString() {
		return "ServiceDetails [serviceName=" + serviceName + ", servicePort=" + servicePort + ", serviceUrl="
				+ serviceUrl + "]";
	}
}
