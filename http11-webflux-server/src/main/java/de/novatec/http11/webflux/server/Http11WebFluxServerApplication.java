package de.novatec.http11.webflux.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"net.uweeisele.http11.webflux.server.config",
		"net.uweeisele.http11.webflux.server.rest"})
public class Http11WebFluxServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(Http11WebFluxServerApplication.class, args);
	}
}
