package com.jmedina.hotel_template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class HotelTemplateApplication {
		
	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing() // No falla si no existe (útil en producción)
                .load();

        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );

		SpringApplication.run(HotelTemplateApplication.class, args);
	}

}
