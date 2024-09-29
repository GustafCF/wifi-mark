package com.br.api.wifi_marketing.config;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.br.api.wifi_marketing.models.RoleModel;
import com.br.api.wifi_marketing.models.UserModel;
import com.br.api.wifi_marketing.repositories.RoleRepository;
import com.br.api.wifi_marketing.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Configuration
@Profile("test")
public class AdminUserConfig implements CommandLineRunner {

    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    public AdminUserConfig(RoleRepository roleRepository, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        
        // roleRepository.deleteAll();

        RoleModel r1 = new RoleModel(1L, "admin");
        RoleModel r2 = new RoleModel(2L, "basic");
        
        roleRepository.saveAll(Arrays.asList(r1,r2));

        var roleAdmin = roleRepository.findByName(RoleModel.Values.ADMIN.name());

        var userAdmin = userRepository.findByUserName("admin");

        userAdmin.ifPresentOrElse(
                user -> {
                    System.out.println("admin ja existe");
                },
                () -> {
                    var user = new UserModel();
                    user.setUserName("admin");
                    user.setPassword(passwordEncoder.encode("123"));
                    user.getRoles().add(roleAdmin);
                    userRepository.save(user);
                }
        );
    }

}
