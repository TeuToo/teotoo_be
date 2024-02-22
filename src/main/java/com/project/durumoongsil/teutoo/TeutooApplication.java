package com.project.durumoongsil.teutoo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TeutooApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeutooApplication.class, args);
	}

}
