package com.lopezinho.friendly_expense_tracker;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableScheduling
public class FriendlyExpenseTrackerApplication {
	public static void main(String[] args) {
		SpringApplication.run(FriendlyExpenseTrackerApplication.class, args);
	}
}