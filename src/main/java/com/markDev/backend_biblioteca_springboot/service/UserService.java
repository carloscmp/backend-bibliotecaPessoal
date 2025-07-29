package com.markDev.backend_biblioteca_springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.markDev.backend_biblioteca_springboot.dto.AuthRequest;
import com.markDev.backend_biblioteca_springboot.entity.UserEntity;
import com.markDev.backend_biblioteca_springboot.repository.UserRepository;

@Service
public class UserService {
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    public UserEntity registerUser(AuthRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Erro: Nome de utilizador já existe!");
        }
        UserEntity newUser = new UserEntity();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        return userRepository.save(newUser);
    }
}