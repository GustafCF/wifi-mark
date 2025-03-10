package com.br.api.wifi_marketing.controllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.br.api.wifi_marketing.config.WebSecurityConfig;
import com.br.api.wifi_marketing.controllers.UserController;
import com.br.api.wifi_marketing.models.RoleModel;
import com.br.api.wifi_marketing.models.UserModel;
import com.br.api.wifi_marketing.models.dtos.CreateUserDto;
import com.br.api.wifi_marketing.services.UserService;
import com.br.api.wifi_marketing.services.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("removal")
@WebMvcTest(UserController.class)
@Import(WebSecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserModel user;
    private CreateUserDto createUserDto;

    @BeforeEach
    void setUp() {
        // Configuração inicial para os testes
        RoleModel role = new RoleModel(1L, RoleModel.Values.BASIC.name());
        user = new UserModel("testuser", "encodedPassword");
        user.setId(1L);
        user.getRoles().add(role);

        createUserDto = new CreateUserDto("testuser", "password");
    }

    @Test
    void testCadastroSuccess() throws Exception {
        // Mock do comportamento do UserService
        when(userService.cadastrar(any(CreateUserDto.class))).thenReturn(user);

        // Executa a requisição POST e verifica a resposta
        mockMvc.perform(post("/us/cad")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isCreated()) // Verifica se o status HTTP é 201
                .andExpect(header().exists("Location")) // Verifica se o header Location existe
                .andExpect(jsonPath("$.username").value("testuser")) // Verifica o username no corpo da resposta
                .andExpect(jsonPath("$.password").value("encodedPassword")); // Verifica a senha no corpo da resposta

        // Verifica se o método foi chamado
        verify(userService, times(1)).cadastrar(any(CreateUserDto.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testFindAll() throws Exception {
        // Mock do comportamento do UserService
        when(userService.findAll()).thenReturn(Collections.singletonList(user));
    
        // Executa a requisição GET e verifica a resposta
        mockMvc.perform(get("/us/list"))
                .andExpect(status().isOk()) // Verifica se o status HTTP é 200
                .andExpect(jsonPath("$[0].username").value("testuser")) // Verifica o username no corpo da resposta
                .andExpect(jsonPath("$[0].password").value("encodedPassword")); // Verifica a senha no corpo da resposta
    
        // Verifica se o método foi chamado
        verify(userService, times(1)).findAll();
    }
    
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testFindByIdSuccess() throws Exception {
        // Mock do comportamento do UserService
        when(userService.findById(1L)).thenReturn(user);
    
        // Executa a requisição GET e verifica a resposta
        mockMvc.perform(get("/us/find/{id}", 1L))
                .andExpect(status().isOk()) // Verifica se o status HTTP é 200
                .andExpect(jsonPath("$.username").value("testuser")) // Verifica o username no corpo da resposta
                .andExpect(jsonPath("$.password").value("encodedPassword")); // Verifica a senha no corpo da resposta
    
        // Verifica se o método foi chamado
        verify(userService, times(1)).findById(1L);
    }
    
    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testFindByIdNotFound() throws Exception {
        // Mock do comportamento do UserService (usuário não encontrado)
        when(userService.findById(1L)).thenThrow(new ResourceNotFoundException(1L));
    
        // Executa a requisição GET e verifica a resposta
        mockMvc.perform(get("/us/find/{id}", 1L))
                .andExpect(status().isNotFound()); // Verifica se o status HTTP é 404
    
        // Verifica se o método foi chamado
        verify(userService, times(1)).findById(1L);
    }
    
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"}) // Requer role ADMIN para exclusão
    void testDeleteSuccess() throws Exception {
        // Executa a requisição DELETE e verifica a resposta
        mockMvc.perform(delete("/us/delete/{id}", 1L))
                .andExpect(status().isNoContent()); // Verifica se o status HTTP é 204
    
        // Verifica se o método foi chamado
        verify(userService, times(1)).delete(1L);
    }
    
    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"}) // Requer role ADMIN para exclusão
    void testDeleteNotFound() throws Exception {
        // Mock do comportamento do UserService (usuário não encontrado)
        doThrow(new ResourceNotFoundException(1L)).when(userService).delete(1L);
    
        // Executa a requisição DELETE e verifica a resposta
        mockMvc.perform(delete("/us/delete/{id}", 1L))
                .andExpect(status().isNotFound()); // Verifica se o status HTTP é 404
    
        // Verifica se o método foi chamado
        verify(userService, times(1)).delete(1L);
    }
}    