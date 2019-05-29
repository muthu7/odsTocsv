package com.lulu.odsTocsv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class OdsTocsvApplication {

	public static void main(String[] args) {
		SpringApplication.run(OdsTocsvApplication.class, args);
	}

}
