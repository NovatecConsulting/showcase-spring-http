package net.uweeisele.http11.delegate.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(value = "/delegate")
public class DemoResource {

    private RestTemplate restTemplate;
    private String serviceURL;

    public DemoResource(
            @Autowired RestTemplate restTemplate,
            @Value("${service.url.http11-server}") String serviceURL) {
        this.restTemplate = restTemplate;
        this.serviceURL = serviceURL;
    }

    @RequestMapping(method = GET, path = "/demo")
    public String getDemo() {
        return this.restTemplate.getForObject( serviceURL + "/demo", String.class);
    }

}