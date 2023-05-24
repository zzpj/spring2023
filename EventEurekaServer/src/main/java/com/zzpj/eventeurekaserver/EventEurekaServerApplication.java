package com.zzpj.eventeurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EventEurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventEurekaServerApplication.class, args);
	}

}
