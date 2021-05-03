package net.uweeisele.http11.server.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static java.util.Optional.ofNullable;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(value = "/demo")
public class DemoResource {

    @RequestMapping(method = GET)
    public String getDemo(
            @RequestParam(required = false, defaultValue = "PT10S") Duration processDuration,
            @RequestParam(required = false) String resultPostfix) {
        try {
            TimeUnit.MILLISECONDS.sleep(processDuration.toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "demo" + ofNullable(resultPostfix).map(v -> "-" + v).orElse("");
    }

}