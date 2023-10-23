package com.ssafy.goodnews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GoodnewsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoodnewsApplication.class, args);
	}

}
