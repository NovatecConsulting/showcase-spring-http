package net.uweeisele.http11.delegate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Component
public class DemoServiceAdapter {

    private final RestTemplate restTemplate;
    private final String serviceURL;

    public DemoServiceAdapter(
            @Autowired RestTemplate restTemplate,
            @Value("${service.url.http11-server}") String serviceURL) {
        this.restTemplate = restTemplate;
        this.serviceURL = serviceURL;
    }

    @Async
    public ListenableFuture<String> getDemoAsync(Duration processDuration) {
        return new AsyncResult<>(getDemo(processDuration));
    }

    public String getDemo(Duration processDuration) {
        return restTemplate.getForObject( serviceURL + "/demo?processDuration=" + processDuration, String.class);
    }

}