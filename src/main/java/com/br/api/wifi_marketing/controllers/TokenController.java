package com.br.api.wifi_marketing.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.api.wifi_marketing.models.dtos.LoginRequest;
import com.br.api.wifi_marketing.models.dtos.LoginResponse;
import com.br.api.wifi_marketing.services.AuthService;

@RestController
@RequestMapping(value = "/auth")
public class TokenController {

    @Autowired
    private AuthService service;

    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = service.login(loginRequest);
        return ResponseEntity.ok().body(loginResponse);
    }

}
