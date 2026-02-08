package com.salestonetech.salestone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Habilita o suporte para execução de tarefas agendadas (@Scheduled)
public class SalestoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalestoneApplication.class, args);
	}

}