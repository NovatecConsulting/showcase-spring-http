package de.novatec.jolokia.webflux;

import org.jolokia.http.HttpRequestHandler;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@RestControllerEndpoint(id = JolokiaEndpoint.ENDPOINT_NAME)
public record JolokiaEndpoint(HttpRequestHandler requestHandler,  Scheduler scheduler) {

    public static final String ENDPOINT_NAME = "jolokia";

    @GetMapping(path = "/**", produces = "text/plain")
    public Mono<String> get(ServerWebExchange exchange) {
        return Mono.fromCallable(() -> handleGetRequest(exchange.getRequest().getURI(), exchange.getRequest().getQueryParams()))
                .subscribeOn(scheduler);
    }

    private String handleGetRequest(URI uri, MultiValueMap<String, String> queryParams) {
        final String pathInfo = pathInfo(uri);
        return requestHandler
                .handleGetRequest(uri.toString(), pathInfo, getParameterMap(queryParams))
                .toJSONString();
    }

    @PostMapping(path = "/**", produces = "text/plain", consumes = "text/json")
    public Mono<String> post(ServerWebExchange exchange) {
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .map(ds -> handlePostRequest(exchange.getRequest().getURI(), exchange.getRequest().getQueryParams(), ds))
                .subscribeOn(scheduler);
    }

    private String handlePostRequest(URI uri, MultiValueMap<String, String> queryParams, DataBuffer dataBuffer) {
        try (InputStream inputStream = dataBuffer.asInputStream(true)) {
            return requestHandler
                    .handlePostRequest(uri.toString(), inputStream, null, getParameterMap(queryParams))
                    .toJSONString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String pathInfo(URI uri) {
        return pathInfo(uri.getPath());
    }

    private static String pathInfo(String path) {
        final int idx = path.indexOf(ENDPOINT_NAME);
        String jolokiaPath = path.substring(idx + ENDPOINT_NAME.length());
        if (jolokiaPath.startsWith("/")) {
            jolokiaPath = jolokiaPath.substring(1);
        }
        return jolokiaPath;
    }

    private static Map<String, String[]> getParameterMap(MultiValueMap<String, String> queryParams) {
        return queryParams.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, v -> v.getValue().toArray(new String[]{})));
    }

}