package com.br.api.wifi_marketing.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.br.api.wifi_marketing.models.UserModel;
import com.br.api.wifi_marketing.models.dtos.CreateUserDto;
import com.br.api.wifi_marketing.services.UserService;

@RestController
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping(value = "/cad")
    public ResponseEntity<UserModel> cadastro(@RequestBody CreateUserDto dto){
        UserModel user = service.cadastrar(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

}
