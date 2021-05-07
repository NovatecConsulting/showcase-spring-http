package net.uweeisele.http11.webflux.delegate.rest;

import net.uweeisele.http11.webflux.delegate.service.DemoServiceAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(value = "/delegate")
public class DemoResource {

    private final DemoServiceAdapter demoService;

    @Autowired
    public DemoResource(DemoServiceAdapter demoService) {
        this.demoService = demoService;
    }

    @RequestMapping(method = GET, path = "/demo")
    public Mono<String> getDemo(@RequestParam(required = false, defaultValue = "PT10S") Duration processDuration) {
        return demoService.getDemo(processDuration);
    }

    @RequestMapping(method = GET, path = "/subcalldemo")
    public Flux<String> getSubCallDemo(
            @RequestParam(required = false, defaultValue = "50") int subCalls,
            @RequestParam(required = false, defaultValue = "PT0.1S") Duration subCallDuration) {
        return Flux.range(0, subCalls)
                .flatMap(n -> demoService.getDemo(subCallDuration, String.valueOf(n)));
    }

    @RequestMapping(method = GET, path = "/subcallincrdemo", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getSubCallIncrDemo(
            @RequestParam(required = false, defaultValue = "50") int subCalls,
            @RequestParam(required = false, defaultValue = "PT0.1S") Duration initSubCallDuration,
            @RequestParam(required = false, defaultValue = "PT0.1S") Duration appendSubCallDuration) {
        AtomicReference<Duration> subCallDuration = new AtomicReference<>(initSubCallDuration);
        return Flux.range(0, subCalls)
                .flatMap(n -> demoService.getDemo(subCallDuration.getAndAccumulate(appendSubCallDuration, Duration::plus), String.valueOf(n)));
    }

    @RequestMapping(method = GET, path = "/subcalldecrdemo", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getSubCallDecrDemo(
            @RequestParam(required = false, defaultValue = "50") int subCalls,
            @RequestParam(required = false, defaultValue = "PT10S") Duration initSubCallDuration) {
        AtomicReference<Duration> subCallDuration = new AtomicReference<>(initSubCallDuration);
        return Flux.range(0, subCalls)
                .flatMap(n -> demoService.getDemo(subCallDuration.getAndUpdate(d -> d.dividedBy(2)), String.valueOf(n)));
    }
}