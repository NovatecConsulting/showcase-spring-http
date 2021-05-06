package net.uweeisele.http11.webflux.server.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static java.util.Optional.ofNullable;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(value = "/demo")
public class DemoResource {

    @RequestMapping(method = GET)
    public Mono<String> getDemo(
            @RequestParam(required = false, defaultValue = "PT10S") Duration processDuration,
            @RequestParam(required = false) String resultPostfix) {
        return Mono
                .just("demo" + ofNullable(resultPostfix).map(v -> "-" + v).orElse(""))
                .delayElement(processDuration);
    }

}