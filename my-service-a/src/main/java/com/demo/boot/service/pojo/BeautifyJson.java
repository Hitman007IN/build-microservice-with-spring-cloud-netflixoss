package com.demo.boot.service.pojo;

import java.util.List;

public class BeautifyJson {

	private List<ServiceDetails> listOfServices;

	public List<ServiceDetails> getListOfServices() {
		return listOfServices;
	}

	public void setListOfServices(List<ServiceDetails> listOfServices) {
		this.listOfServices = listOfServices;
	}
	
	@Override
	public String toString() {
		return "listOfServices=" + listOfServices;
	}
}
