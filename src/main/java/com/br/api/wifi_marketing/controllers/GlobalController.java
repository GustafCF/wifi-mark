package com.br.api.wifi_marketing.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GlobalController {

    @GetMapping("/")
    public String login(){
        return "index";
    }

    @GetMapping("/cadastro")
    public String cadastro(){
        return "cadastro";
    }

}
