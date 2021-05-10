package de.novatec.http11.webflux.delegate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
		"net.uweeisele.http11.webflux.delegate.config",
		"net.uweeisele.http11.webflux.delegate.rest",
		"net.uweeisele.http11.webflux.delegate.service"})
public class Http11WebFluxDelegateApplication {

	public static void main(String[] args) {
		//see reactor.netty.ReactorNetty
		//see reactor.netty.resources.LoopResources
		//see io.netty.channel.SingleThreadEventLoop
		//System.setProperty("reactor.netty.ioWorkerCount", "8");
		SpringApplication.run(Http11WebFluxDelegateApplication.class, args);
	}
}
