package com.zzpj.eventuserservice.keycloak;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeycloakUserService {

    private final Keycloak keycloak;

    public KeycloakUserService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public List<UserRepresentation> findByUsername(String name, Boolean exact) {

        return keycloak.realm("event_app")
                .users()
                .searchByUsername(name, exact);
    }
}
