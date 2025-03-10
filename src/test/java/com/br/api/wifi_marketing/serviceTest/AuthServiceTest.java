package com.br.api.wifi_marketing.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import com.br.api.wifi_marketing.models.RoleModel;
import com.br.api.wifi_marketing.models.UserModel;
import com.br.api.wifi_marketing.models.dtos.LoginRequest;
import com.br.api.wifi_marketing.models.dtos.LoginResponse;
import com.br.api.wifi_marketing.repositories.UserRepository;
import com.br.api.wifi_marketing.services.AuthService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private UserModel user;
    private RoleModel role;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
       
        role = new RoleModel(1L, "ROLE_ADMIN");
        Set<RoleModel> roles = new HashSet<>();
        roles.add(role);

        user = new UserModel("testuser", "encodedPassword");
        user.setId(1L);
        user.getRoles().addAll(roles);

        loginRequest = new LoginRequest("testuser", "password");
    }

    @Test
    void testLoginSuccess() {
        // Mock do comportamento do UserRepository
        when(userRepository.findByUsername(loginRequest.username())).thenReturn(Optional.of(user));

        // Mock do comportamento do PasswordEncoder
        when(passwordEncoder.matches(loginRequest.password(), user.getPassword())).thenReturn(true);

        // Mock do comportamento do JwtEncoder
        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("fakeJwtToken");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        // Executa o método de login
        LoginResponse response = authService.login(loginRequest);

        // Verifica se o token foi gerado corretamente
        assertNotNull(response);
        assertEquals("fakeJwtToken", response.acessToken());
        assertEquals(300L, response.expiresIn());

        // Verifica se os métodos foram chamados
        verify(userRepository, times(1)).findByUsername(loginRequest.username());
        verify(passwordEncoder, times(1)).matches(loginRequest.password(), user.getPassword());
        verify(jwtEncoder, times(1)).encode(any(JwtEncoderParameters.class));
    }

    @Test
    void testLoginUserNotFound() {
        // Mock do comportamento do UserRepository (usuário não encontrado)
        when(userRepository.findByUsername(loginRequest.username())).thenReturn(Optional.empty());

        // Verifica se a exceção é lançada
        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));

        // Verifica se o método foi chamado
        verify(userRepository, times(1)).findByUsername(loginRequest.username());
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtEncoder, never()).encode(any());
    }

    @Test
    void testLoginInvalidPassword() {
        // Mock do comportamento do UserRepository
        when(userRepository.findByUsername(loginRequest.username())).thenReturn(Optional.of(user));

        // Mock do comportamento do PasswordEncoder (senha inválida)
        when(passwordEncoder.matches(loginRequest.password(), user.getPassword())).thenReturn(false);

        // Verifica se a exceção é lançada
        assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));

        // Verifica se os métodos foram chamados
        verify(userRepository, times(1)).findByUsername(loginRequest.username());
        verify(passwordEncoder, times(1)).matches(loginRequest.password(), user.getPassword());
        verify(jwtEncoder, never()).encode(any());
    }
}
