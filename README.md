# Spring 2023 - notatki z zajęć
## Prezentacje

- [Spring Boot 101 - Wprowadzenie](https://github.com/zzpj/spring2023/blob/master/ZZPJ2023-springboot.pdf)
- [Spring Boot 102 - Przegląd zaawansowanych funkcjonalności](https://github.com/zzpj/spring2023/blob/master/ZZPJ2023-springboot-advanced.pdf)
- [Mikroserwisy - prezentacja](https://github.com/zzpj/spring2023/blob/master/ZZPJ2023-microservices.pdf) [Livecoding](https://github.com/zzpj/spring2023/blob/master/microservices.md)

## Live coding
Otwórz: https://start.spring.io/

- Project: Maven
- Language: Java
- Spring Boot: 3.0.6
- Project Metadata:
  - group: com.zzpj
  - artifact: EventManager
  - name: EventManager
  - description: demo project
  - package com.zzpj.eventmanager
  - packaging: Jar
  - java: 17
- Dependencies:
  - Lombok
  - Spring Web
  - Spring Data JPA
  - H2 Database
  - Spring Boot Actuator

Kliknij: **Generate**

* Warto jeszcze spoglądać na: https://calendar.spring.io/

## Analiza pobranej paczki
- Rozpakowanie zip i otwarcie w IDE
- pom.xml
- `@SpringBootApplication`
- Uruchomienie
- Sprawdzenie w przeglądarce

## Convention over Configuration
- uzupełnij plik `application.properties`:
  - `server.port=8020`
  - `spring.application.name=event-manager-service`

## Pierwszy Kontroler
```java
@RestController
public class TestController {
  @GetMapping("/hello")
  public String getServiceName(){
    return "Hello world";
  }
}
```

## Testowanie API z użyciem Postman'a:
https://www.postman.com/downloads/

## Bardziej zaawansowany kontroler
```java
@RestController
public class TestController {

  @Value("${spring.application.name}")
  private String applicationName;

  @GetMapping("/hello/{name}")
  public String getServiceName(@PathVariable("name") String name){
    return "Hello" + name + "\n you are using " + applicationName;
  }
}
```

## Adnotacje
Event Model:
```java
@Data
@Builder(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class Event {

  private Long id;
  private String name;
  private String description;
  private Double entranceFee;
  private LocalDateTime startDate;
}
```

EventService:
```java
@AllArgsConstructor
public class EventService {

  private List<Event> events;

  public List<Event> getAllEvents() {
    return events;
  }

  public Event getEvent(Long id) {
    List<Event> collect = events.stream().filter(e -> id.equals(e.getId())).toList();
    return collect.size() == 1 ? collect.get(0) : null;
  }

  public void addEvent(Event event) {
    events.add(event);
  }

  public void deleteEvent(Long id) {
    Event event = getEvent(id);
    if (event != null) {
      events.remove(event);
    }
  }
}
```
Config:
```java
@Configuration
public class Config {

  @Bean
  public EventService eventService() {
    List<Event> eventList = new ArrayList<>();
    eventList.add(new Event(1L, "event-name", "desc", 120.0d,
            LocalDateTime.of(2023, 12, 12, 20, 00)));
    eventList.add(new Event(2L, "second-event-name", "desc", 100.0d,
            LocalDateTime.of(2023, 12, 24, 10, 00)));
    return new EventService(eventList);
  }
}
```

EventController:
```java
@RestController
@Slf4j
public class EventController {

  @Autowired
  EventService eventService;
  @GetMapping("/getAllEvents")
  public List<Event> getAllEvents(){
    return eventService.getAllEvents();
  }

  @GetMapping("/getEvent/{id}")
  public Event getEvent(@PathVariable("id") Long id) {
    log.info("Id: " + id);
    return eventService.getEvent(id);
  }

  @PostMapping("/addEvent")
  public void addEvent(@RequestBody Event event) {
    eventService.addEvent(event);
  }

  @DeleteMapping("/delete/{id}")
  public void removeEvent(@PathVariable("id") Long id) {
    eventService.deleteEvent(id);
  }
}
```

## Swagger - przejrzysta prezentacja zasobów API
Dodaj do pom.xml:
```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.1.0</version>
</dependency>
```
Sprawdź: http://localhost:8020/swagger-ui/index.html

Uzupełnij dokumentację API:
```java
    @Operation(
        summary = "Get Service Name and say hello to user",
        description = "Get Service Name and say hello to user")
@ApiResponses({
        @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = String.class), mediaType = "application/json") }),
        @ApiResponse(responseCode = "404", description = "Not found"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error") })
@GetMapping("/hello/{name}")
public String getServiceName(@PathVariable("name") String name){
        return "Hello" + name + "\n you are using " + applicationName;
        }
```

```java
@Bean
public OpenAPI openAPI() {

        Contact contact = new Contact();
        contact.name("Zbyszko");
        contact.email("your@email.com");

        Info info = new Info();
        info.setTitle("title");
        info.setDescription("my desc");
        info.setVersion("1.2.3");
        info.setContact(contact);

        OpenAPI openAPI = new OpenAPI();
        openAPI.setInfo(info);
        return openAPI;
        }
```

## JPA: Warstwa danych
Entity:
```java
@Data
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String description;
  private Double entranceFee;
  private LocalDateTime startDate;
}
```

Repository:
```java
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}
```

Service:
```java
@AllArgsConstructor
@Service
public class EventService {

  private final EventRepository eventRepository;

  public List<Event> getAllEvents() {
    return eventRepository.findAll();
  }

  public Event getEvent(Long id) {
    return eventRepository.findById(id).orElse(null);
  }

  public void addEvent(Event event) {
    eventRepository.save(event);
  }

  public Event updateEvent(Event event) {
    Event updatedEvent = eventRepository.save(event);
    return updatedEvent;
  }

  public void deleteEvent(Long id) {
    eventRepository.deleteById(id);
  }
}
```

Controller:
```java
@RestController
@Slf4j
@AllArgsConstructor
public class EventController {

  private final EventService eventService;

  @GetMapping("/getAllEvents")
  public List<Event> getAllEvents() {
    return eventService.getAllEvents();
  }

  @GetMapping("/getEvent/{id}")
  public Event getEvent(@PathVariable("id") Long id) {
    log.info("Id: " + id);
    return eventService.getEvent(id);
  }

  @PostMapping("/addEvent")
  public void addEvent(@RequestBody Event event) {
    eventService.addEvent(event);
  }

  @PutMapping("update/{id}")
  public Event updateEvent(@PathVariable Long id, @RequestBody Event event) {
    return eventService.updateEvent(event);
  }


  @DeleteMapping("/delete/{id}")
  public void removeEvent(@PathVariable("id") Long id) {
    eventService.deleteEvent(id);
  }
}
```
### Walidacja danych
Dodać:
```xml
<dependency>
  <groupId>org.hibernate</groupId>
  <artifactId>hibernate-validator</artifactId>
  <version>8.0.0.Final</version>
</dependency>
```
Przy okazji naprawia to błąd: `Unable to create a Configuration, because no Jakarta Bean Validation...`

Model:
```java
@Data
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class Event {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Size(min = 4, max = 50)
  private String name;

  @NotBlank(message = "description cannot be empty")
  private String description;

  @Positive(message = "please remember about minimal price")
  private Double entranceFee;

  @NotNull
  private LocalDateTime startDate;
}
```

Dodaj adnotację `@Valid`
```java
@PostMapping("/addEvent")
public void addEvent(@Valid @RequestBody Event event) {
        eventService.addEvent(event);
        }
```
Domyślne wiadomości w walidatorze: `hibernate-validator-8.0.0.Final.jar!\org\hibernate\validator\ValidationMessages.properties`

Model odpowiedzi błędnej:
```java
@Data
public class EventErrorResponse {

  LocalDateTime localDateTime;
  Integer status;
  List<String> errors;
}
```

Handler obsługi błędu:
```java
@ControllerAdvice
public class EventExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

    EventErrorResponse eventErrorResponse = new EventErrorResponse();
    eventErrorResponse.setLocalDateTime(LocalDateTime.now());
    eventErrorResponse.setStatus(status.value());

    eventErrorResponse.setErrors(ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .toList());


    return new ResponseEntity<>(eventErrorResponse, headers, status);
  }
}
```


Controller:
```java
    @Operation(
        summary = "Adds event",
        description = "Adds event to event list")
@ApiResponses({
        @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = String.class), mediaType = "application/json") }),
        @ApiResponse(responseCode = "400", content = { @Content(schema = @Schema(implementation = EventErrorResponse.class), mediaType = "application/json") }),
        @ApiResponse(responseCode = "500", description = "Internal Server Error") })
@PostMapping("/addEvent")
public void addEvent(@Valid @RequestBody Event event) {
        eventService.addEvent(event);
        }
```
Więcej o walidacji: https://mkyong.com/spring-boot/spring-rest-validation-example/

## Actuators
* Teoria: https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html
* Domyślny link: http://localhost:8020/actuator
* Rozszerzenie w _application.properties_: `management.endpoints.web.exposure.include=*`
* Ciekawsze endpointy (opisz wszystkich: https://www.baeldung.com/spring-boot-actuators#3-predefined-endpoints):
  * http://localhost:8020/actuator/health
  * http://localhost:8020/actuator/beans
  * http://localhost:8020/actuator/mappings
  * http://localhost:8020/actuator/env
    * aby zobaczyć ukryte wartości (od wersji 3.0) należy dodać do pliku properties: `management.endpoint.env.show-values=ALWAYS`
  * http://localhost:8020/actuator/info
    ```properties
    management.info.env.enabled=true
    info.app.name=${spring.application.name}
    info.app.description=This is event manager service
    info.app.version=0.0.1-alpha
    ```
    ```java
    @Value("${info.app.name}") private String appName;
    @Value("${info.app.description}") private String appDescription;
    @Value("${info.app.version}") private String appVersion;
    
    @Bean
    public OpenAPI openAPI() {

        Contact contact = new Contact();
        contact.name("Zbyszko");
        contact.email("your@email.com");

        Info info = new Info();
        info.setTitle(appName);
        info.setDescription(appDescription);
        info.setVersion(appVersion);
        info.setContact(contact);

        OpenAPI openAPI = new OpenAPI();
        openAPI.setInfo(info);
        return openAPI;
    }
    ```


## Profiles

Przykładowe nazwy profili: `dev`,`int`,`prod`

### Scenariusz nr 1
Utwórz pliki properties: `application-dev.properties`, `application-int.properties` oraz `application-prod.properties` o poniższej zawartości:
```properties
# application-dev.properties:
info.app.description=This is event manager service on DEV stage
```
```properties
# application-int.properties:
info.app.description=This is event manager service on INT stage
```
```properties
# application-prod.properties:
info.app.description=This is event manager service on PROD stage
```
Uruchom Spring-Boot z użyciem IntelliJ i ustaw pozycję `Active profiles` na `dev`, następnie przejdź na `http://localhost:8020/swagger-ui/index.html`

### Scenariusz nr 2
Użycie adnotacji `@Profile` w klasie `Config`
```java
@Configuration
public class Config {

  @Value("${info.app.name}")
  private String appName;
  @Value("${info.app.description}")
  private String appDescription;
  @Value("${info.app.version}")
  private String appVersion;

  @Bean
  @Profile("!prod")
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

  @Bean
  @Profile("prod")
  public OpenAPI prodOpenAPI() {
    Info info = new Info();
    info.setTitle(appName);
    info.setDescription(appDescription);
    info.setVersion(appVersion);

    Contact contact = new Contact();
    contact.name("Zbyszko on PROD");
    contact.email("mymail-prod@mail-server.info");
    info.setContact(contact);

    OpenAPI openAPI = new OpenAPI();
    openAPI.setInfo(info);
    return openAPI;
  }
}
```
Usuń wartość `dev` z `active profiles` w Intellij i dodaj do pliku `application.properties`:

```properties
spring.profiles.active=prod
```

### Scenariusz nr 3 Multi-document files
Usuń pliki: `application-dev.properties`, `application-int.properties` oraz `application-prod.properties` i ustaw wartości properties w jednym pliku, w zależności od środowiska:
```properties
spring.application.name=event-manager-service
management.endpoints.web.exposure.include=*
management.endpoint.env.show-values=ALWAYS
management.info.env.enabled=true
info.app.name=${spring.application.name}
info.app.description=This is event manager service
info.app.version=0.0.1-alpha
#---
spring.config.activate.on-profile=dev
info.app.description=This is event manager service on DEV
server.port=8021
#---
spring.config.activate.on-profile=int
info.app.description=This is event manager service on INT
server.port=8022
#---
spring.config.activate.on-profile=prod
info.app.description=This is event manager service on PROD
server.port=8023
```
Przygotuj różne konfiguracje uruchomieniowe w IntelliJ
Zapisz tą część na oddzielnym (różnym od `master`) branch'u i wróć na poprzednią implementację na branchu `master`

## OpenAPI - SpringBoot jako stub-server
1. Wstęp teoretyczny w prezentacji:
2. Przegląd narzędzi do OpenAPI
3. Stwórz `api.yaml` w `src/main/resource` albo ręcznie albo z wykorzystaniem IDE
4. Uzupełnij:
```yaml
openapi: 3.0.3
info:
  title: place-manager
  description: place description
  version: 1.0.0

paths:
  /places:
    get:
      summary: get all places
      operationId: getAllPlaces
      responses:
        200:
          description: all places for events
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/Place'
    post:
      summary: create a new place
      operationId: createPlace
      responses:
        200:
          description: Created
        500:
          description: Internal Server Error
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PlaceError'
        409:
          description: id conflict
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PlaceError'
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/Place'

  /places/{placeId}:
    parameters:
      - name: placeId
        description: the place unique id
        in: path
        required: true
        schema:
          type: string
    get:
      summary: get place for event
      operationId: getPlaceById
      responses:
        200:
          description: the place for event provided successfully
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Place'
        404:
          description: the place not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PlaceError'
        500:
          description: Internal Server Error
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PlaceError'
    delete:
      summary: deletes place for event
      operationId: deletePlaceById
      responses:
        200:
          description: delete the place for event
        404:
          description: the place not found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PlaceError'
        500:
          description: Internal Server Error
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/PlaceError'
components:
  schemas:
    Place:
      type: object
      required:
        - id
        - name
        - capacity
        - placeType
      properties:
        id:
          type: string
        name:
          type: string
        capacity:
          type: number
        placeType:
          type: string
          enum:
            - stadium
            - outdoor
            - town square
            - park
    PlaceError:
      type: object
      required:
        - message
      properties:
        message:
          description: error message
          type: string
```
5. Wykorzystaj wbudowane narzędzie i albo uzupełnianie `pom.xml` o brakujące _dependencies_ albo [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator)
```xml
<openapi-generator-version>6.5.0</openapi-generator-version>
```
```xml
<dependency>
  <groupId>org.openapitools</groupId>
  <artifactId>openapi-generator</artifactId>
  <version>${openapi-generator-version}</version>
</dependency>
<dependency>
<groupId>org.openapitools</groupId>
<artifactId>jackson-databind-nullable</artifactId>
<version>0.2.6</version>
</dependency>
```
```xml
<plugin>
  <groupId>org.openapitools</groupId>
  <artifactId>openapi-generator-maven-plugin</artifactId>
  <version>${openapi-generator-version}</version>
  <executions>
    <execution>
      <goals>
        <goal>generate</goal>
      </goals>
      <configuration>
        <inputSpec>src/main/resources/api.yaml</inputSpec>
        <generatorName>spring</generatorName>
        <apiPackage>com.zzpj.openapi</apiPackage>
        <modelPackage>com.zzpj.openapi.model</modelPackage>
        <configOptions>
          <sourceFolder>src/gen/java/main</sourceFolder>
          <useJakartaEe>true</useJakartaEe>
          <interfaceOnly>true</interfaceOnly>
        </configOptions>
      </configuration>
    </execution>
  </executions>
</plugin>
```
6. Pamiętaj o "Reload All Maven Project" oraz usunięciu `spring-boot-maven-plugin` (konflikt sf4j)
7. Wygeneruj API za pomocą `mvn clean install`
8. Stwórz `PlaceController` i nadpisz wygenerowane metody oraz przeanalizuj co się wygenerowało

### MapStruct
Dodaj do `pom.xml`:
```xml
        <dependency>
  <groupId>org.mapstruct</groupId>
  <artifactId>mapstruct</artifactId>
  <version>1.5.5.Final</version>
</dependency>
<dependency>
<groupId>org.mapstruct</groupId>
<artifactId>mapstruct-processor</artifactId>
<version>1.5.5.Final</version>
</dependency>
```
Stwórz klasę modelową: `PlaceDAO`
```java
@Data
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class PlaceDAO {

  @Id
  private String id;

  private String name;

  private Double capacity;

  private String placeType;

}
```
Następnie `PlaceRepository`, `PlaceMapper`, `PlaceService`:
```java
@Repository
public interface PlaceRepository extends JpaRepository<PlaceDAO, String> {
}
```
```java
@Mapper(componentModel = "spring")
public interface PlaceMapper {

  PlaceMapper INSTANCE = Mappers.getMapper( PlaceMapper.class );

  @Mapping(source = "placeType", target = "placeType")
  PlaceDAO toPlaceDAO(Place place);

  @Mapping(source = "placeType", target = "placeType")
  Place toPlace(PlaceDAO placeDAO);
}
```
```java
@Service
@AllArgsConstructor
public class PlaceService {
  private final PlaceMapper placeMapper;
  private final PlaceRepository placeRepository;

  public void addNewPlace(Place place) {
    PlaceDAO placeDAO = placeMapper.toPlaceDAO(place);
    placeRepository.save(placeDAO);
  }

  public Place getPlaceById(String id) {
    return placeRepository.findById(id).map(placeMapper::toPlace).orElseThrow();
  }

  public List<Place> getAllPlaces() {
    return placeRepository.findAll().stream().map(placeMapper::toPlace).toList();
  }

  public void deleteById(String id) {
    placeRepository.deleteById(id);
  }
}
```
Wprowadź zmiany w `PlaceController`:
```java
@RestController
@RequiredArgsConstructor
public class PlaceController implements PlacesApi {

  private final PlaceService placeService;

  @Override
  public ResponseEntity<Void> createPlace(Place place) {
    placeService.addNewPlace(place);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> deletePlaceById(String placeId) {
    placeService.deleteById(placeId);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<List<Place>> getAllPlaces() {
    return ResponseEntity.ok(placeService.getAllPlaces());
  }

  @Override
  public ResponseEntity<Place> getPlaceById(String placeId) {
    Place placeById = placeService.getPlaceById(placeId);
    return ResponseEntity.ok(placeById);
  }
}
```

## Rest Clients (również z wykorzystaniem OpenAPI)
Otwórz: https://start.spring.io/

- Project: Maven
- Language: Java
- Spring Boot: 3.0.6
- Project Metadata:
  - group: com.zzpj
  - artifact: eventclient
  - name: eventclient
  - description: demo project
  - package com.zzpj.eventmanager
  - packaging: Jar
  - java: 17
- Dependencies:
  - Spring Web
  - Spring Reactive Web

Kliknij: **Generate**

Rozpakuj, uruchom w IDE:
```properties
server.port=8025
com.zzpj.serverUrl=http://localhost:8020/
```
```java
@Value("${com.zzpj.serverUrl}")
private String serverUrl;
```

### RestTemplate
```java
@Bean
public RestTemplate restTemplate(RestTemplateBuilder builder) {
	return builder.build();
}

@Bean
public CommandLineRunner run(RestTemplate restTemplate) {
	return args -> {
		ResponseEntity<MyEvent> forEntity = restTemplate.getForEntity(serverUrl + "getEvent/1", MyEvent.class);
		System.out.println("Response: " + forEntity.getBody());
	};
}
```
```java
public record MyEvent(Long id, String name, String description, Double entranceFee, LocalDateTime startDate) {}
```

### WebClient
```java
@Bean
public WebClient webClient() {
	return WebClient.builder()
			.baseUrl(serverUrl)
			.build();
}

@Bean
public CommandLineRunner run2(WebClient webClient) {
	return args -> {
		Mono<MyEvent> myEventMono = webClient
				.get()
				.uri("/getEvent/1")
				.retrieve()
				.bodyToMono(MyEvent.class);

		System.out.println(myEventMono.block()); //TODO: lepszy sposób
	};
}
```

### via OpenAPI
```xml
<openapi-generator-version>6.5.0</openapi-generator-version>
```
```xml
<dependency>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator</artifactId>
    <version>${openapi-generator-version}</version>
</dependency>
<dependency>
    <groupId>org.openapitools</groupId>
    <artifactId>jackson-databind-nullable</artifactId>
    <version>0.2.6</version>
</dependency>
```
```xml
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>${openapi-generator-version}</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>src/main/resources/api.yml</inputSpec>
                <generatorName>java</generatorName>
                <apiPackage>com.zzpj.openapi</apiPackage>
                <modelPackage>com.zzpj.openapi.model</modelPackage>
                <generateApiTests>false</generateApiTests>
                <generateModelTests>false</generateModelTests>
                <configOptions>
                    <sourceFolder>src/gen/java/main</sourceFolder>
                    <useJakartaEe>true</useJakartaEe>
                    <library>resttemplate</library>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```

Dodaj informację o serwerze:
```yaml
servers:
  - url: http://localhost:8020/
    description: local dev instance
```

Wygeneruj api: `mvn clean install` oraz dodaj:

```java
@Bean
public DefaultApi defaultApi() {
    return new DefaultApi();
}

@Bean public CommandLineRunner run3(DefaultApi defaultApi) {
    return args -> {
        List<Place> allPlaces = defaultApi.getAllPlaces();
        System.out.println("??");
    };
}
```

## Security
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### Scenariusz nr 1A
```properties
spring.security.user.name=admin
spring.security.user.password=admin123
```
### Scenariusz nr 1B
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public UserDetailsService users(PasswordEncoder passwordEncoder) {
    UserDetails user = User.withUsername("admin")
            .password(passwordEncoder.encode("admin123"))
            .roles("ADMIN")
            .build();
    return new InMemoryUserDetailsManager(user);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
            .authorizeHttpRequests()
            .anyRequest()
            .authenticated()
            .and()
            .formLogin();
    return httpSecurity.build();
  }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
```

### JWT
Dodaj nową zależność:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```
Dodaj nowy endpoint/controller np `TokenController`: 
```java
@AllArgsConstructor
@RestController
public class TokenController {

    private final JwtEncoder jwtEncoder;

    @PostMapping("/auth")
    public String auth(Authentication authentication) {

        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(120))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
```
Przebuduj klasę `SecurityConfig`:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.public.key}")
    private RSAPublicKey rsaPublicKey;

    @Value("${jwt.private.key}")
    private RSAPrivateKey rsaPrivateKey;

  @Bean
  public UserDetailsService users(PasswordEncoder passwordEncoder) {
    UserDetails user = User.withUsername("admin")
            .password(passwordEncoder.encode("admin123"))
            .roles("ADMIN")
            .build();
    return new InMemoryUserDetailsManager(user);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
            .authorizeHttpRequests()
            .requestMatchers("/auth", "/v3/api-docs.yaml",
                    "/v3/api-docs/**","/swagger-ui/**", "/swagger-ui.html").permitAll()
            .anyRequest().hasAuthority("SCOPE_ROLE_ADMIN").and()
            .csrf().disable()
            .httpBasic(Customizer.withDefaults())
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions
                    .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                    .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
            )
    ;
    return httpSecurity.build();
  }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(this.rsaPublicKey).privateKey(this.rsaPrivateKey).build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
```
Wygeneruj parę kluczy: https://www.csfieldguide.org.nz/en/interactives/rsa-key-generator/ 
- key size: 2048 bits
- format scheme: pkcs #8 (base64)
Utwórz pliki w _src/main/resources_: `api.key` oraz `app.pub`, skopiuj odpowiednio zawartość

Dodaj w `application.properties`:
```properties
jwt.private.key=classpath:app.key
jwt.public.key=classpath:app.pub
```

Za pomocą Postman wygeneruj token i przeanalizuj go na: https://jwt.io/. Nastepnie razem z tokenem odpal dowolny endpoint

### JWT + OpenApi
W `SecurityConfig` uzupełnij w metodzie `filterChain`:
```java
.requestMatchers("/auth", "/v3/api-docs.yaml", "/v3/api-docs/**","/swagger-ui/**", "/swagger-ui.html").permitAll()
```

W klasie `Config`:
```java
@OpenAPIDefinition(
        security = {
                @SecurityRequirement(name = "bearerToken")
        }
)
@SecuritySchemes({
        @SecurityScheme(name = "bearerToken", type = SecuritySchemeType.HTTP, scheme = "bearer",
                bearerFormat = "JWT")
})
```
Odpal swaggera http://localhost:8020/swagger-ui/index.html i powtórz scenariusz z Postmanem

(c) by Zbyszko Natkański
