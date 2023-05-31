package com.zzpj.eventuserservice;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    @GetMapping("/external")
    public String external() {
        return "external message";
    }

    @GetMapping("/internal")
    public String internal() {
        return "secrect message";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "redirect:/";
    }

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String issuerUrl;

    @GetMapping("/profile")
    private void profile(HttpServletResponse response) {
        response.setHeader("Location", issuerUrl + "/account");
        response.setStatus(302);
    }
}
