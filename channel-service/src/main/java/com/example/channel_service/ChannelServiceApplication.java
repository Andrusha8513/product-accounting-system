package com.example.channel_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients
@ComponentScan(basePackages = {"com.example.channel_service", "com.example.support_module"})
public class ChannelServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChannelServiceApplication.class, args);
	}

}
