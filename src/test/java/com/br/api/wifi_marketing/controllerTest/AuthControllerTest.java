package com.br.api.wifi_marketing.controllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import com.br.api.wifi_marketing.config.WebSecurityConfig;
import com.br.api.wifi_marketing.controllers.AuthController;
import com.br.api.wifi_marketing.models.dtos.LoginRequest;
import com.br.api.wifi_marketing.models.dtos.LoginResponse;
import com.br.api.wifi_marketing.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
@Import(WebSecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc; // Simula requisições HTTP

    @SuppressWarnings("removal")
    @MockBean
    private AuthService authService; // Mock do serviço

    @Autowired
    private ObjectMapper objectMapper; // Converte objetos Java para JSON e vice-versa

    @Test
    void testLoginSuccess() throws Exception {
        // Dados de entrada e saída simulados
        LoginRequest loginRequest = new LoginRequest("testuser", "password");
        LoginResponse loginResponse = new LoginResponse("fakeJwtToken", 300L);

        // Mock do comportamento do AuthService
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // Executa a requisição POST e verifica a resposta
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk()) // Verifica se o status HTTP é 200
                .andExpect(jsonPath("$.acessToken").value("fakeJwtToken")) // Verifica o token
                .andExpect(jsonPath("$.expiresIn").value(300L)); // Verifica o tempo de expiração
    }

    @Test
    void testLoginInvalidCredentials() throws Exception {
        // Dados de entrada simulados
        LoginRequest loginRequest = new LoginRequest("testuser", "wrongpassword");

        // Mock do comportamento do AuthService (lança exceção)
        when(authService.login(any(LoginRequest.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Executa a requisição POST e verifica a resposta
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized()); // Verifica se o status HTTP é 401
    }
}