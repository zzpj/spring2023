package com.zzpj.eventclient;

import com.zzpj.openapi.DefaultApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
public class EventclientApplication {


    @Value("${com.zzpj.url}")
    private String serverUrl;

    public static void main(String[] args) {
        SpringApplication.run(EventclientApplication.class, args);
    }


    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .basicAuthentication("admin", "admin123")
                .build();
    }

    @Bean
    public ServiceInstanceListSupplier serviceInstanceListSupplier() {
        return new EventManagerSupplier("event-manager-service");
    }

    @Bean
    public String jwtToken(RestTemplate restTemplate) {
        ResponseEntity<String> response = restTemplate.postForEntity(serverUrl + "/auth", null, String.class);
        return response.getBody();
    }

    @Bean
    public DefaultApi defaultApi(String jwtToken) {

        DefaultApi defaultApi = new DefaultApi();
        defaultApi.getApiClient().setBearerToken(jwtToken);
        return defaultApi;
    }

//    @Bean
//    public CommandLineRunner run3(DefaultApi api) {
//        return args -> {
//            List<Place> allPlaces = api.getAllPlaces();
//            allPlaces.forEach(System.out::println);
//        };
//    }
}