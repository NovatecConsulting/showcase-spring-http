package net.uweeisele.http11.delegate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"net.uweeisele.http11.delegate.config",
		"net.uweeisele.http11.delegate.rest",
		"net.uweeisele.http11.delegate.service"})
public class Http11DelegateApplication {

	public static void main(String[] args) {
		SpringApplication.run(Http11DelegateApplication.class, args);
	}
}
