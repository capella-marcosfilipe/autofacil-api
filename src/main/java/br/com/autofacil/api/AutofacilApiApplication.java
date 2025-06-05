package br.com.autofacil.api;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AutofacilApiApplication {
	// Dotenv dotenv = Dotenv.load();

	public static void main(String[] args) {
		SpringApplication.run(AutofacilApiApplication.class, args);
	}

}
