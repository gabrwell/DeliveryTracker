package com.gabcompany.delivery_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@SpringBootApplication
@EnableJpaAuditing
public class DeliveryTrackerApplication {



	public static void main(String[] args) {
		SpringApplication.run(DeliveryTrackerApplication.class, args);
	}

}
