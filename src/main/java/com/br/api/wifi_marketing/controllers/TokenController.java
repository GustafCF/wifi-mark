package com.br.api.wifi_marketing.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.br.api.wifi_marketing.models.UserModel;
import com.br.api.wifi_marketing.models.dtos.CreateUserDto;
import com.br.api.wifi_marketing.models.dtos.LoginRequest;
import com.br.api.wifi_marketing.models.dtos.LoginResponse;
import com.br.api.wifi_marketing.services.AuthService;

@CrossOrigin("*")
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

    @PostMapping(value = "/cad")
    public ResponseEntity<UserModel> cadastro(@RequestBody CreateUserDto dto) {
        var obj = service.createUser(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, JwtAuthenticationToken token) {
        service.deleteUser(id, token);
        return ResponseEntity.noContent().build();
    }

}
