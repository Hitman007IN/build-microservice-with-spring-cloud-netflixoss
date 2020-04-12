package com.demo.boot.support.authenticate.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AppUser {

	private Integer id;
	private String username, password;
	private String role;

}
