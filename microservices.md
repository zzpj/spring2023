# Microservices 2023

## Read & watch

### How to start
* [https://microservices.io/](https://microservices.io/)
* [12factor.net](https://12factor.net/)
### Online courses
* https://app.pluralsight.com/library/courses/getting-started-microservices
* https://app.pluralsight.com/library/courses/microservices-fundamentals
* https://app.pluralsight.com/library/courses/building-reactive-microservices (Java demo app)
* https://app.pluralsight.com/library/courses/microservices-security-fundamentals
* https://app.pluralsight.com/library/courses/java-microservices-spring-cloud-developing-services
* https://app.pluralsight.com/library/courses/java-microservices-spring-cloud-coordinating-services
### Youtube
* https://youtu.be/P4iomsHmOW0
* https://youtu.be/GBTdnfD6s5Q
### Spring Cloud documentation
* https://spring.io/projects/spring-cloud
### Presentation
* [Prezentation from 2023](https://github.com/zzpj/spring2023/blob/master/ZZPJ2023-microservices.pdf)

## Live coding scenario

### Discovery Server Installation and Configuration
1. Enter [Spring Initializr website](https://start.spring.io/)
1. Complete Metadata section
1. (2023) Select Spring Boot version: *3.0.7* and determine your jdk version: *(17)*
1. Select following dependencies: Spring Web & Eureka Server
1. Click Generate button and download zipped package.
1. Unzip package and open generated project in IntelliJ
1. Open main class with `@SpringBootApplication` annotation
1. Use Spring Cloud’s `@EnableEurekaServer` to stand up a registry with which other applications can communicate. This is a regular Spring Boot application with one annotation added to enable the service registry.
1. By default, the registry also tries to register itself, so you need to disable that behavior as well in  `application.properties` file.
	```properties
	eureka.client.register-with-eureka=false
	eureka.client.fetch-registry=false
	```
1. Select the port which will be used by Eureka Server 
	```properties
	server.port=8761
	```
1. Enter the URL: `http://localhost:8761/`

### Register first service: EventManager
1. Open `EventManager` project
1. Complete `pom.xml`:
	```xml
	<spring-cloud.version>2022.0.2</spring-cloud.version>
	```
	```xml
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
	</dependency>
	```
	```xml
	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>${spring-cloud.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
	```
1. Add annotation `@EnableDiscoveryClient` to main class
1. Add some properties into `application.properties`
	```properties
	eureka.client.serviceUrl.defaultZone=${EUREKA_URL:http://localhost:8761/eureka/}
	eureka.client.fetch-registry=true
	eureka.client.register-with-eureka=true
	```
1. Run `EventManager` application and determine if service has been registered in Eureka Discovery Server by entering `http://localhost:8761/` or using logs.
1. Complete `SecurityConfig` class by adding missing excluded path:
	```java
	 .requestMatchers("/auth", "/v3/api-docs.yaml", "/actuator/info",
	```

### Register second service: EventClient
1. there is some auth steps to take first:
   1. Add to api.yaml
       ```yaml
      security:
        - bearerAuth: []
       ```
	   ```yaml
		 securitySchemes:
		   bearerAuth:
		    type: http
		    scheme: bearer
		    bearerFormat: JWT
	   ```
   1. Update `EventClientApplication`:
      ```java
       @Value("${com.zzpj.url}")
       private String serverUrl;
	
       public static void main(String[] args) {
         SpringApplication.run(EventClientApplication.class, args);
       }
	
       @Bean
       public RestTemplate restTemplate(RestTemplateBuilder builder) {
         return builder
                    .basicAuthentication("admin", "admin123")
                    .build();
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
	
       @Bean
       public CommandLineRunner getAll(DefaultApi api) {
           return args -> {
             List<Place> allPlaces = api.getAllPlaces();
             allPlaces.forEach(System.out::println);
           };
       }
      ```
    1. Check if it is working by adding new place and displaying all
1. Complete `pom.xml`:
	```xml
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
	</dependency>
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-actuator</artifactId>
	</dependency>
	```
	```xml
	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>${spring-cloud.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>
	```
	
	```xml
	<spring-cloud.version>2022.0.2</spring-cloud.version>
	```
1. Add annotation `@EnableDiscoveryClient` to main class
1. Add to `application.properties`:
	```properties
	server.port=8035
	
	spring.application.name=event-client-app
	
	management.endpoints.web.exposure.include=*
	management.endpoint.env.show-values=ALWAYS
	
	management.info.env.enabled=true
	info.app.name=${spring.application.name}
	info.app.description=This is event client app
	info.app.version=0.0.1-alpha
	
	eureka.client.serviceUrl.defaultZone=${EUREKA_URL:http://localhost:8761/eureka/}
	eureka.client.fetch-registry=true
	eureka.client.register-with-eureka=true
	```
1. Add new controller:
	```java
	@RestController
	public class EventClientController {
	
		private final DefaultApi defaultApi;
	
		public EventClientController(DefaultApi defaultApi) {
			this.defaultApi = defaultApi;
		}
	
		@GetMapping("/getAllPlaces")
		public String getAllPlaces() {
			return defaultApi.getAllPlaces().toString();
		}
	}
	```
1. Verify and go to URL: `http://localhost:8035/getAllPlaces`

### Spring Cloud Client Load balancer 
1. Stop running `EventManager` and comment `server.port` properties
1. Provide changes in `TestController`:
	```java
	@RestController
	public class TestController {
	
		@Value("${spring.application.name}")
		private String applicationName;
	
		@Value("${server.port}")
		private String appPort;
	
		@Operation(summary = "get service name", description = "tris is get service name endpoint")
		@ApiResponses({
				@ApiResponse(responseCode = "200", description = "stardard sucessful output"),
				@ApiResponse(responseCode = "404", description = "Not found"),
				@ApiResponse(responseCode = "500", description = "Internal Server Error")
		})
		@GetMapping("/hello/{name}")
		public String getServiceName(@PathVariable("name") String name) {
			return "Hello " + name + " \n you are using " + applicationName + " on port: " + appPort;
		}
	}
	```
1. Unsecure path in `SecurityConfig`:
	```java
	.requestMatchers("/auth", "/v3/api-docs.yaml", "/actuator/info", "/hello/**",
	```
1. Run two (or more) instances using Spring Boot Run Configuration, use Environment > VM Options for setting ports: `-Dserver.port=8021`
1. Use `services tab` in IntelliJ to run them all
1. Refresh Eureka Discovery page and determine if both instances of the same service are available
1. Add load balancer dependency in `EventClient` project
	```xml
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-loadbalancer</artifactId>
	</dependency>
	```
1. Modify `restTemplate` object and add new bean: 
	```java
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
	```
1. `EventManagerSupplier` implementation:
	```java
	public class EventManagerSupplier implements ServiceInstanceListSupplier {
	
		private final String serviceId;
	
		public EventManagerSupplier(String serviceId) {
			this.serviceId = serviceId;
		}
	
		@Override
		public String getServiceId() {
			return serviceId;
		}
	
		@Override
		public Flux<List<ServiceInstance>> get() {
			ServiceInstance int1 = new DefaultServiceInstance(serviceId + "1", serviceId, "localhost", 8020, false);
			ServiceInstance int2 = new DefaultServiceInstance(serviceId + "2", serviceId, "localhost", 8021, false);
			ServiceInstance int3 = new DefaultServiceInstance(serviceId + "3", serviceId, "localhost", 8022, false);
			return Flux.just(Stream.of(int1, int2, int3).toList());
		}
	}
	```
1. Changes in: `EventClientController`:
	```java
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
			ResponseEntity<String> forEntity = restTemplate.getForEntity("http://event-manager-service/hello/xxx", String.class);
			return forEntity.getBody();
		}
	}
	```
1. Verify and go to URL: `http://localhost:8035/getHello`
2. OpenAPI case by modifying `api.yaml`:
	```yaml
	servers:
      - url: http://localhost:{port}/
          description: local dev instance
          variables:
            port:
              default: '8020'
              enum:
                - '8020'
                - '8021'
                - '8022'
	```
1. Run mvn:  `mvn clean install`
2. Rerun `EventClient` and go to URL: `http://localhost:8035/getAllPlaces`


### Config Server
#### Prepare Config Server Implementation
1. Open [Spring Initializr website](https://start.spring.io/)
1. Complete Metadata section: set Artifact name as `EventConfigServer`
1. Select following dependencies: Spring Web, Eureka Discovery Client, Config Server
1. Click Generate button, download and unzip package
1. Copy unzipped `EventConfigServer` folder into your project folder
1. Add following annotations: `@@EnableDiscoveryClient` & `@EnableConfigServer` into main class
1. Add some properties into `application.properties`
	```properties
	server.port=8061
	spring.application.name=event-config-server

	eureka.client.serviceUrl.defaultZone=${EUREKA_URL:http://localhost:8761/eureka/}

	spring.cloud.config.server.git.uri=https://github.com/zzpj/demo-config-server.git
	#spring.cloud.config.server.git.uri=file://E://ZaawansowanaJava22//demo-config-server-local
	spring.cloud.config.server.git.default-label=main
	#spring.cloud.config.server.git.default-label=master
	spring.cloud.config.server.git.clone-on-start=true
	```
1. Run config server project & determine eureka main page
#### Prepare either local repo or github once
1. local repo: go to new folder and `git init`
1. OR create new repo in github

#### Move properties of created services 
1. Complete `pom.xml` of `EventClient`
	```xml
	<dependency>
		<groupId>org.springframework.cloud</groupId>
		<artifactId>spring-cloud-starter-config</artifactId>
	</dependency>
	```
1. Go to `application.properties` files and add:
	```properties
	spring.config.import=optional:configserver:
	spring.cloud.config.discovery.enabled=true
	spring.cloud.config.discovery.service-id=event-config-server
	spring.cloud.config.name=${spring.application.name}
	spring.cloud.config.profile=dev
	spring.cloud.config.label=main
	spring.cloud.config.fail-fast=true
	```
1. Add properties check with use of `@Value` annotation
	```java
    @Value("${config.server.demo}")
    private String greetings;
 
    @GetMapping("/getGreetings")
    public String getGreetings() {
        return "Property value: " + greetings;
    }
	```
1. Remember about following properties naming rules
	```
	/{application}/{profile}[/{label}]
	/{application}-{profile}.yml
	/{label}/{application}-{profile}.yml
	/{application}-{profile}.properties
	/{label}/{application}-{profile}.properties
	```

### Keycloak
[Instalacja do pobrania](https://www.keycloak.org/downloads) lub [obraz dockerowy](https://quay.io/repository/keycloak/keycloak), a także niezbędna [dokumentacja](https://www.keycloak.org/guides#getting-started). Następnie, [krok po kroku jak zacząć](https://www.keycloak.org/getting-started/getting-started-zip).

Podstawowe pojęcia:
- Realm
- OAuth
- User/Role/Client/Federation Provider

1. Uruchom keycloak
1. Ustaw kredki dla root admin'a
1. Przedstaw poszczególne linki
1. Stwórz:
   - nowy realm
     - zakładka _General_
       - ustaw: Display name
     - zakładka _Login_
       - User registration: On 
       - Forgot password: On 
       - Remember me: On
       - Verify email: Off
     - zakładka _themes_
       - wszystko prefiksowane keycloak-
     - zakładka _Localization_
       - dodać obsługę języka polskiego
   - nowego klienta
       - zakładka _Credentials_
         - Client Authenticator: client id and secret
         - wygeneruj `Client secret`
       - zakładka _Settings_ 
         - root url: `http://localhost:8090`
         - valid redirect urls: `/*`
         - Client authentication: on
         - Authorization: on
         - Login theme: keycloak
   - nowego użytkownika (pole 'Required user actions' puste) + ew jakieś role
1. Postman:
   ```
   client_id:<your_client_id>
   username:<your_username>
   password:<your_password>
   grant_type:password
   client_secret:<secret>
   ```
1. Aplikacja w springu
   1. start.spring.io: wersja 3.0.7, web, oauth2-client, security, web
   2. plik `application.properties`:
   ```yaml
    spring.application.name=event-user-app
    server.port=8090
	
    spring.security.oauth2.client.registration.keycloak.client-id=event-app-client
    spring.security.oauth2.client.registration.keycloak.client-secret=WDn7qJ0VYrsAdfd3ayaXVKljBKImN3UN
    spring.security.oauth2.client.registration.keycloak.authorization-grant-type=authorization_code
    spring.security.oauth2.client.registration.keycloak.scope=openid
	
    spring.security.oauth2.client.provider.keycloak.issuer-uri=http://localhost:8080/realms/event_users
    ```
   3. klasa `SecurityConfig`:
   ```yaml
    @Configuration
    @EnableWebSecurity
    public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests()
                .requestMatchers("/external").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
        ;
        return http.build();
    }
    }
   ```
   4. klasa `InfoController`:
   ```yaml
    @RestController
    public class InfoController {
    
        @GetMapping("/internal")
        public String getSecretInfo() {
            return "secrets info";
        }
    
        @GetMapping("/external")
        public String getMessageAll() {
            return "message all";
        }
    }
    ```
   5. Sprawdzenie w przeglądarce