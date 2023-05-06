package com.zzpj.EventManager.config;

import com.zzpj.EventManager.model.Event;
import com.zzpj.EventManager.service.EventService;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class Config {

//    @Bean
//    public EventService eventService() {
//
//        List<Event> eventList = new ArrayList<>();
//        eventList.add(new Event(1L, "event-1", "event desc", 100.0d,
//                LocalDateTime.of(2023, 4, 26, 20, 00)));
//        eventList.add(new Event(2L, "event-2", "event desc", 300.0d,
//                LocalDateTime.of(2023, 12, 26, 12, 00)));
//        return new EventService(eventList);
//    }

    @Value("${info.app.name}") private String appName;
    @Value("${info.app.description}") private String appDescription;
    @Value("${info.app.version}") private String appVersion;

    @Bean
    public OpenAPI openAPI() {

        Info info = new Info();
        info.setTitle(appName);
        info.setDescription(appDescription);
        info.setVersion(appVersion);

        Contact contact = new Contact();
        contact.name("Zbyszko");
        contact.email("mymail@mail-server.info");
        info.setContact(contact);

        OpenAPI openAPI = new OpenAPI();

        openAPI.setInfo(info);

        return openAPI;
    }
}
