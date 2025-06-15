package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.model.User; // Importa a entidade User
import com.miaueauau.clinica_veterinaria.repository.UserRepository; // Importa o UserRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/login") // Endpoint para o login
public class LoginController {

    // Altere de FuncionarioRepository para UserRepository
    @Autowired
    private UserRepository userRepository; // AGORA INJETAMOS O USERREPOSITORY

    @PostMapping
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String senha = loginData.get("senha"); // Em um sistema real, a senha deve ser hasheada e comparada com o hash

        // 1. Busca o usuário pelo e-mail no banco de dados
        Optional<User> userOptional = userRepository.findByEmail(email); // AGORA BUSCAMOS POR USER

        // 2. Verifica se o usuário existe E se a senha confere
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Comparação de senha em texto puro (APENAS PARA DESENVOLVIMENTO/TESTE BÁSICO).
            // EM PRODUÇÃO, SEMPRE USE HASHING DE SENHAS (ex: BCryptPasswordEncoder).
            if (user.getPassword().equals(senha)) { // Use getPassword() da entidade User
                Map<String, String> response = new HashMap<>();
                response.put("message", "Login bem-sucedido!");
                response.put("nome", user.getNome()); // Pega o nome do User
                response.put("email", user.getEmail()); // Pega o email do User

                // Se o User tiver um campo para 'administrador' ou 'role', você pode adicionar aqui
                // Exemplo:
                // if (user.getRole() != null) {
                //    response.put("role", user.getRole().name()); // Assumindo um Enum Role
                // }
                // Ou se você tiver um boolean isAdministrador no User:
                // response.put("administrador", String.valueOf(user.isAdministrador())); // Se o User tiver esse método

                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }

        // Se o login falhou
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Credenciais inválidas.");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED); // 401 Unauthorized
    }
}