package com.gabcompany.delivery_tracker.controller;

import com.gabcompany.delivery_tracker.dto.CsrfResponseDTO;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {

    @GetMapping("/auth/csrf")
    public CsrfResponseDTO csrf(CsrfToken csrfToken) {
        return new CsrfResponseDTO(csrfToken.getHeaderName(), csrfToken.getToken());
    }
}
