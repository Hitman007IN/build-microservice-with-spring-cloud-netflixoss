package com.demo.boot.service.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ToString
@Setter
@Getter
@ConfigurationProperties("servicea")
public class VaultConfiguration {

    private String username;

    private String password;
}
