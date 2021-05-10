package de.novatec.http11.delegate.service;

import de.novatec.http11.delegate.support.collections.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

import java.time.Duration;

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
        final UriComponents uri = demoUriTemplate.expand(Maps.of(
                Maps.entry("processDuration", processDuration),
                Maps.entry("resultPostfix", "{resultPostfix}")));

        return restTemplate.getForObject(uri.toUriString(), String.class, Maps.of(Maps.entry("resultPostfix", resultPostfix)));
    }
}