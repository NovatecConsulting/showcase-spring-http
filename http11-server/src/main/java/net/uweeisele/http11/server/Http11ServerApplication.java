package net.uweeisele.http11.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "net.uweeisele.http11.server.rest")
public class Http11ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(Http11ServerApplication.class, args);
	}
}
