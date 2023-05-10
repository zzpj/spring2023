package com.zzpj.eventmanager.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        security = {
                @SecurityRequirement(name = "bearerToken")
        }
)
@SecuritySchemes({
        @SecurityScheme(name = "bearerToken", type = SecuritySchemeType.HTTP, scheme = "bearer",
                bearerFormat = "JWT")
})
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

    @Value("${info.app.name}")
    private String appName;
    @Value("${info.app.description}")
    private String appDescription;
    @Value("${info.app.version}")
    private String appVersion;

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
