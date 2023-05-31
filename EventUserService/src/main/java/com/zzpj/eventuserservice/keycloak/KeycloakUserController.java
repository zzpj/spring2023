package com.zzpj.eventuserservice.keycloak;


import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.QueryParam;
import java.util.List;

@RestController
public class KeycloakUserController {

    private final KeycloakUserService keycloakUserService;


    public KeycloakUserController(KeycloakUserService keycloakUserService) {
        this.keycloakUserService = keycloakUserService;
    }

    @GetMapping("/findUsers/{name}")
    public List<UserRepresentation> findUsers(@PathVariable("name") String name, @QueryParam("exact") Boolean exact) {
        return keycloakUserService.findByUsername(name, exact);
    }
}
