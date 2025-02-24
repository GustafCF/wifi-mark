package com.br.api.wifi_marketing.services;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.br.api.wifi_marketing.models.RoleModel;
import com.br.api.wifi_marketing.models.UserModel;
import com.br.api.wifi_marketing.models.dtos.CreateUserDto;
import com.br.api.wifi_marketing.repositories.RoleRepository;
import com.br.api.wifi_marketing.repositories.UserRepository;

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
        var userFromDB = userRepository.findByUserName(entity.name());
        
        if(userFromDB.isPresent()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        var user = new UserModel();
        user.setUserName(entity.name());
        user.setPassword(passwordEncoder.encode(entity.password()));
        user.getRoles().add(basicRole);
        
        return userRepository.save(user);
    }

}
