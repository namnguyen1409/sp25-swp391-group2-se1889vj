package com.group2.sp25swp391group2se1889vj;

import com.group2.sp25swp391group2se1889vj.scheduler.InvoiceScheduler;
import com.group2.sp25swp391group2se1889vj.service.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EnableScheduling
@AllArgsConstructor
public class Sp25Swp391Group2Se1889vjApplication {

	private final InvoiceScheduler invoiceScheduler;

	public static void main(String[] args) {
		SpringApplication.run(Sp25Swp391Group2Se1889vjApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.init();
		};
	}

	@EventListener(ApplicationReadyEvent.class)
	public void doSomethingAfterStartup() {

		new Thread(() -> {

		}).start();
	}







}
