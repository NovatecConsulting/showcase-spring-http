package net.uweeisele.http11.client.pool.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(value = "/delegate")
public class DemoResource {

    private RestTemplate restTemplate;

    @Autowired
    public DemoResource(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping(method = GET, path = "/demo")
    public String getDemo() {
        return this.restTemplate.getForObject("http://localhost:8800/demo", String.class);
    }

}