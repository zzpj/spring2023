package com.zzpj.eventclient;

import com.zzpj.openapi.DefaultApi;
import com.zzpj.openapi.model.Place;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@SpringBootApplication
public class EventclientApplication {


	@Value("${com.zzpj.url}")
	private String serverUrl;

	public static void main(String[] args) {
		SpringApplication.run(EventclientApplication.class, args);
	}

//	@Bean
//	public RestTemplate restTemplate(RestTemplateBuilder builder) {
//		return builder.build();
//	}
//
//	@Bean
//	public CommandLineRunner run(RestTemplate restTemplate) {
//		return args -> {
//
//			ResponseEntity<MyPlace> forEntity = restTemplate.getForEntity(serverUrl + "/places/110", MyPlace.class);
//			System.out.println(forEntity.getBody());
//		};
//	}

//
//	@Bean
//	public WebClient webClient() {
//		return WebClient.builder()
//				.baseUrl(serverUrl)
//				.build();
//	}
//
//	@Bean
//	public CommandLineRunner run2(WebClient webClient) {
//		return args -> {
//
//			Mono<MyPlace> myPlaceMono = webClient.get().uri("/places/110").retrieve().bodyToMono(MyPlace.class);
//			System.out.println(myPlaceMono.block());
//		};
//	}

	@Bean
	public DefaultApi defaultApi() {
		return new DefaultApi();
	}

	@Bean
	public CommandLineRunner run3(DefaultApi api) {
		return args -> {
			List<Place> allPlaces = defaultApi().getAllPlaces();
			System.out.println();
		};
	}


}
record MyPlace(String id, String name, Double capacity, String placeType){}