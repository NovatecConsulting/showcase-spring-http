package net.uweeisele.http11.client.pool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "net.uweeisele.http11.client.pool", "net.uweeisele.http11.client.pool.rest" })
public class Http11ClientPoolApplication {

	public static void main(String[] args) {
		SpringApplication.run(Http11ClientPoolApplication.class, args);
	}
}
