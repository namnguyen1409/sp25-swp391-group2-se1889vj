package com.group2.sp25swp391group2se1889vj;

import com.group2.sp25swp391group2se1889vj.service.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Sp25Swp391Group2Se1889vjApplication {

	public static void main(String[] args) {
		SpringApplication.run(Sp25Swp391Group2Se1889vjApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.init();
		};
	}


}
