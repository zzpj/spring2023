package com.zzpj.eventmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port}")
    private String portNumber;

    @Operation(summary = "get service name", description = "tris is get service name endpoint")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "stardard sucessful output"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/hello/{name}")
    public String getServiceName(@PathVariable("name") String name) {
        return "Hello " + name + " \n you are using " + applicationName + " port number: " + portNumber;
    }
}
