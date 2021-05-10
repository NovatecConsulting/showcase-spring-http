package de.novatec.webflux.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"de.novatec.webflux.server.config",
		"de.novatec.webflux.server.rest"})
public class WebFluxServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebFluxServerApplication.class, args);
	}
}
