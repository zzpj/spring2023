package com.zzpj.eventclient;


import com.zzpj.openapi.DefaultApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class EventClientController {

    private final DefaultApi defaultApi;

    private final RestTemplate restTemplate;

    public EventClientController(DefaultApi defaultApi, RestTemplate restTemplate) {
        this.defaultApi = defaultApi;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/getAllPlaces")
    public String getAllPlaces() {
        return defaultApi.getAllPlaces().toString();
    }

    @GetMapping("/getHello")
    public String getHello() {
        return restTemplate.getForEntity("http://event-manager-service/hello/sss", String.class).getBody();
    }

    @Value("${config.server.demo}")
    private String greetings;

    @GetMapping("/getGreetings")
    public String getGreetings() {
        return greetings;
    }
}
