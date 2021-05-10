package de.novatec.webflux.delegate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"de.novatec.webflux.delegate.config",
		"de.novatec.webflux.delegate.rest",
		"de.novatec.webflux.delegate.service"})
public class WebFluxDelegateApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebFluxDelegateApplication.class, args);
	}
}
