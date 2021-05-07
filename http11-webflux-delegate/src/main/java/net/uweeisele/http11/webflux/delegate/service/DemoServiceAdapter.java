package net.uweeisele.http11.webflux.delegate.service;

import net.uweeisele.http11.webflux.delegate.support.collections.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

import java.time.Duration;

import static net.uweeisele.http11.webflux.delegate.support.collections.Maps.entry;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Component
public class DemoServiceAdapter {

    private final RestTemplate restTemplate;

    private final UriComponents demoUriTemplate;

    public DemoServiceAdapter(
            @Autowired RestTemplate restTemplate,
            @Value("${service.url.http11-server}") String serviceURL) {
        this.restTemplate = restTemplate;

        this.demoUriTemplate = fromHttpUrl(serviceURL)
                .path("demo")
                .queryParam("processDuration", "{processDuration}")
                .queryParam("resultPostfix", "{resultPostfix}").build();
    }

    @Async
    public ListenableFuture<String> getDemoAsync(Duration processDuration) {
        return new AsyncResult<>(getDemo(processDuration));
    }

    @Async
    public ListenableFuture<String> getDemoAsync(Duration processDuration, String resultPostfix) {
        return new AsyncResult<>(getDemo(processDuration, resultPostfix));
    }

    public String getDemo(Duration processDuration) {
        return getDemo(processDuration, null);
    }

    public String getDemo(Duration processDuration, String resultPostfix) {
        UriComponents uri = demoUriTemplate.expand(Maps.of(
                entry("processDuration", processDuration),
                entry("resultPostfix", "{resultPostfix}")));

        return restTemplate.getForObject(uri.toUriString(), String.class, Maps.of(entry("resultPostfix", resultPostfix)));
    }
}