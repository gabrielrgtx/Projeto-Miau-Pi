package com.miaueauau.clinica_veterinaria.service;

import com.miaueauau.clinica_veterinaria.model.User;
import com.miaueauau.clinica_veterinaria.repository.UserRepository;
// import org.springframework.security.crypto.password.PasswordEncoder; // Remova esta importação se não usar

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    // Removido: private final PasswordEncoder passwordEncoder; // REMOVA ESTA LINHA TAMBÉM

    // Construtor AJUSTADO: Agora espera APENAS UserRepository
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Método para registrar um novo usuário
    public User registerUser(User user) {
        // A senha já deve estar em texto plano no 'user' que chega aqui
        // Removido: user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Método para buscar usuário por email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}