package com.arya.ehcache.delegation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class SpringBootH2EhcacheDelegationApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootH2EhcacheDelegationApplication.class, args);
	}

}
