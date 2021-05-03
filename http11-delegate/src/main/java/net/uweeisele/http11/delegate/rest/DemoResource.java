package net.uweeisele.http11.delegate.rest;

import net.uweeisele.http11.delegate.service.DemoServiceAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
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
    public String getDemo(@RequestParam(required = false, defaultValue = "PT10S") Duration processDuration) {
        return demoService.getDemo(processDuration);
    }

    @RequestMapping(method = GET, path = "/subcalldemo")
    public List<String> getSubCallDemo(
            @RequestParam(required = false, defaultValue = "50") int subCalls,
            @RequestParam(required = false, defaultValue = "PT0.1S") Duration subCallDuration) {
        List<ListenableFuture<String>> results = IntStream.range(0, subCalls)
                .mapToObj(n -> demoService.getDemoAsync(subCallDuration, String.valueOf(n)))
                .collect(toList());

        return results.stream()
                .map(ListenableFuture::completable)
                .map(CompletableFuture::join)
                .collect(toList());
    }

}