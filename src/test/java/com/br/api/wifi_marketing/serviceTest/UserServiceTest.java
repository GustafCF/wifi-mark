package com.br.api.wifi_marketing.serviceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.br.api.wifi_marketing.models.RoleModel;
import com.br.api.wifi_marketing.models.UserModel;
import com.br.api.wifi_marketing.models.dtos.CreateUserDto;
import com.br.api.wifi_marketing.repositories.RoleRepository;
import com.br.api.wifi_marketing.repositories.UserRepository;
import com.br.api.wifi_marketing.services.UserService;
import com.br.api.wifi_marketing.services.exceptions.DatabaseException;
import com.br.api.wifi_marketing.services.exceptions.ForbiddenException;
import com.br.api.wifi_marketing.services.exceptions.ResourceNotFoundException;
import com.br.api.wifi_marketing.services.exceptions.UnauthorizedException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserModel user;
    private RoleModel role;
    private CreateUserDto createUserDto;

    @BeforeEach
    void setUp() {
        // Configuração inicial para os testes
        role = new RoleModel(1L, RoleModel.Values.BASIC.name());
        user = new UserModel("testuser", "encodedPassword");
        user.setId(1L);
        user.getRoles().add(role);

        createUserDto = new CreateUserDto("testuser", "password");
    }

    @Test
    void testCadastrarSuccess() {
        // Mock do comportamento do RoleRepository
        when(roleRepository.findByName(RoleModel.Values.BASIC.name())).thenReturn(role);

        // Mock do comportamento do PasswordEncoder
        when(passwordEncoder.encode(createUserDto.password())).thenReturn("encodedPassword");

        // Mock do comportamento do UserRepository
        when(userRepository.save(any(UserModel.class))).thenReturn(user);

        // Executa o método de cadastro
        UserModel result = userService.cadastrar(createUserDto);

        // Verifica o resultado
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.getRoles().contains(role));

        // Verifica se os métodos foram chamados
        verify(roleRepository, times(1)).findByName(RoleModel.Values.BASIC.name());
        verify(passwordEncoder, times(1)).encode(createUserDto.password());
        verify(userRepository, times(1)).save(any(UserModel.class));
    }

    @Test
    void testFindAll() {
        // Mock do comportamento do UserRepository
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        // Executa o método de listagem
        List<UserModel> result = userService.findAll();

        // Verifica o resultado
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(user, result.get(0));

        // Verifica se o método foi chamado
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdSuccess() {
        // Mock do comportamento do UserRepository
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Executa o método de busca por ID
        UserModel result = userService.findById(1L);

        // Verifica o resultado
        assertNotNull(result);
        assertEquals(user, result);

        // Verifica se o método foi chamado
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        // Mock do comportamento do UserRepository (usuário não encontrado)
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Verifica se a exceção é lançada
        assertThrows(ResourceNotFoundException.class, () -> userService.findById(1L));

        // Verifica se o método foi chamado
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteSuccess() {
        // Executa o método de exclusão
        userService.delete(1L);

        // Verifica se o método foi chamado
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteNotFound() {
        // Mock do comportamento do UserRepository (usuário não encontrado)
        doThrow(EmptyResultDataAccessException.class).when(userRepository).deleteById(1L);

        // Verifica se a exceção é lançada
        assertThrows(ResourceNotFoundException.class, () -> userService.delete(1L));

        // Verifica se o método foi chamado
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteDataIntegrityViolation() {
        // Mock do comportamento do UserRepository (violação de integridade)
        doThrow(DataIntegrityViolationException.class).when(userRepository).deleteById(1L);

        // Verifica se a exceção é lançada
        assertThrows(DatabaseException.class, () -> userService.delete(1L));

        // Verifica se o método foi chamado
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUnauthorized() {
        // Mock do comportamento do UserRepository (acesso não autorizado)
        doThrow(UnauthorizedException.class).when(userRepository).deleteById(1L);

        // Verifica se a exceção é lançada
        assertThrows(UnauthorizedException.class, () -> userService.delete(1L));

        // Verifica se o método foi chamado
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteForbidden() {
        // Mock do comportamento do UserRepository (acesso negado)
        doThrow(ForbiddenException.class).when(userRepository).deleteById(1L);

        // Verifica se a exceção é lançada
        assertThrows(ForbiddenException.class, () -> userService.delete(1L));

        // Verifica se o método foi chamado
        verify(userRepository, times(1)).deleteById(1L);
    }
}