package com.br.api.wifi_marketing.services;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.br.api.wifi_marketing.models.RoleModel;
import com.br.api.wifi_marketing.models.UserModel;
import com.br.api.wifi_marketing.models.dtos.CreateUserDto;
import com.br.api.wifi_marketing.models.dtos.LoginRequest;
import com.br.api.wifi_marketing.models.dtos.LoginResponse;
import com.br.api.wifi_marketing.repositories.RoleRepository;
import com.br.api.wifi_marketing.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public AuthService(JwtEncoder jwtEncoder, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        var user = userRepository.findByUserName(loginRequest.name());

        if (user.isEmpty() || !user.get().LoginValidation(loginRequest, passwordEncoder)) {
            throw new BadCredentialsException("user or password is invalid!");
        }

        var now = Instant.now();
        var expiresIn = 300L;

        var scopes = user.get().getRoles()
                .stream()
                .map(RoleModel::getName)
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer("back-end")
                .subject(user.get().getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scope", scopes)
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new LoginResponse(jwtValue, expiresIn);
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
