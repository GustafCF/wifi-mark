package com.br.api.wifi_marketing.services;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

import com.br.api.wifi_marketing.models.RoleModel;
import com.br.api.wifi_marketing.models.UserModel;
import com.br.api.wifi_marketing.models.dtos.CreateUserDto;
import com.br.api.wifi_marketing.repositories.RoleRepository;
import com.br.api.wifi_marketing.repositories.UserRepository;
import com.br.api.wifi_marketing.services.exceptions.DatabaseException;
import com.br.api.wifi_marketing.services.exceptions.ForbiddenException;
import com.br.api.wifi_marketing.services.exceptions.ResourceNotFoundException;
import com.br.api.wifi_marketing.services.exceptions.UnauthorizedException;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserModel cadastrar(CreateUserDto entity){
            var basicRole = roleRepository.findByName(RoleModel.Values.BASIC.name());
            var user = new UserModel();
            user.setUsername(entity.username());
            user.setPassword(passwordEncoder.encode(entity.password()));
            user.getRoles().add(basicRole);
            return userRepository.save(user);
        
    }

    public List<UserModel> findAll(){
        return userRepository.findAll();
    }

    public UserModel findById(Long id){
        Optional<UserModel> user = userRepository.findById(id);
        return user.orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public void delete(Long id){
        try{
            userRepository.deleteById(id);
        } catch(EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        } catch (Unauthorized e) {
            throw new UnauthorizedException(e.getMessage());
        } catch(AccessDeniedException e) {
            throw new ForbiddenException(e.getMessage());
        }
    }

}
