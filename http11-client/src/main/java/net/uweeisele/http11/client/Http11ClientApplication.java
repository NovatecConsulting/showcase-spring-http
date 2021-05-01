package net.uweeisele.http11.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "net.uweeisele.http11.client.config", "net.uweeisele.http11.client.rest" })
public class Http11ClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(Http11ClientApplication.class, args);
	}
}
