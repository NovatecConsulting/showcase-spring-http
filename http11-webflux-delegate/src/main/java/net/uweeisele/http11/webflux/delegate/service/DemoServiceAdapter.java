package net.uweeisele.http11.webflux.delegate.service;

import net.uweeisele.http11.webflux.delegate.support.collections.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

import static net.uweeisele.http11.webflux.delegate.support.collections.Maps.entry;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Component
public class DemoServiceAdapter {

    private final WebClient webClient;

    private final UriComponents demoUriTemplate;

    public DemoServiceAdapter(
            @Autowired WebClient webClient,
            @Value("${service.url.http11-server}") String serviceURL) {
        this.webClient = webClient;

        this.demoUriTemplate = fromHttpUrl(serviceURL)
                .path("demo")
                .queryParam("processDuration", "{processDuration}")
                .queryParam("resultPostfix", "{resultPostfix}").build();
    }

    public Mono<String> getDemo(Duration processDuration) {
        return getDemo(processDuration, null);
    }

    public Mono<String> getDemo(Duration processDuration, String resultPostfix) {
        UriComponents uri = demoUriTemplate.expand(Maps.of(
                entry("processDuration", processDuration),
                entry("resultPostfix", "{resultPostfix}")));
        return webClient.get()
                .uri(uri.toUriString(), Maps.of(entry("resultPostfix", resultPostfix)))
                .retrieve()
                .bodyToMono(String.class);
    }
}