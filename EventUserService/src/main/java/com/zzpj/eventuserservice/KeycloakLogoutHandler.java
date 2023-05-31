package com.zzpj.eventuserservice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;


@Component
public class KeycloakLogoutHandler implements LogoutHandler {


    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String issuerUrl;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String logoutEndpoint = issuerUrl + "/protocol/openid-connect/logout";

        OidcUser principal = (OidcUser) authentication.getPrincipal();

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(logoutEndpoint)
                .queryParam("id_token_hint", principal.getIdToken().getTokenValue());

        ResponseEntity<String> logoutResponse = new RestTemplateBuilder().build().getForEntity(builder.toUriString(), String.class);
        if (logoutResponse.getStatusCode().is2xxSuccessful()) {
            System.out.println("ok");
        } else {
            System.out.println("not ok");
        }


    }
}
