package com.arya.ehcache.delegation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@SpringBootApplication
@EnableJpaRepositories
public class SpringBootH2EhcacheDelegationApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootH2EhcacheDelegationApplication.class, args);
	}

}