package de.novatec.web.delegate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"de.novatec.web.delegate.config",
		"de.novatec.web.delegate.rest",
		"de.novatec.web.delegate.service"})
public class WebDelegateApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebDelegateApplication.class, args);
	}
}
