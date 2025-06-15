package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.dto.LoginRequest;
import com.miaueauau.clinica_veterinaria.dto.RegisterRequest;
import com.miaueauau.clinica_veterinaria.model.User;
import com.miaueauau.clinica_veterinaria.model.UserRole;
import com.miaueauau.clinica_veterinaria.model.Funcionario;
import com.miaueauau.clinica_veterinaria.model.Tutor;
import com.miaueauau.clinica_veterinaria.model.Veterinario;
import com.miaueauau.clinica_veterinaria.repository.UserRepository;
import com.miaueauau.clinica_veterinaria.repository.FuncionarioRepository;
import com.miaueauau.clinica_veterinaria.repository.TutorRepository;
import com.miaueauau.clinica_veterinaria.repository.VeterinarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UsernameNotFoundException; // Importe este
import org.springframework.web.bind.MethodArgumentNotValidException; // NOVO: Importar esta exceção


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String authenticatedUserEmail = authentication.getName();
            User authenticatedUser = userRepository.findByEmail(authenticatedUserEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário autenticado não encontrado."));

            Map<String, String> response = new HashMap<>();
            response.put("message", "Login realizado com sucesso!");
            response.put("email", authenticatedUser.getEmail());
            response.put("role", authenticatedUser.getRole().name());
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (AuthenticationException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Credenciais inválidas.");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody RegisterRequest registerRequest, BindingResult result) {
        // NOVO: Bloco para depurar erros de validação
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            System.err.println("DEBUG (Register): Erros de validação:");
            result.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
                System.err.println("  - Campo: " + error.getField() + ", Erro: " + error.getDefaultMessage() + ", Valor rejeitado: " + error.getRejectedValue());
            });
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        // Validações de unicidade (se o front-end passar, significa que não falhou validação de @NotBlank)
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "E-mail já cadastrado.");
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }
        if (userRepository.findByCpf(registerRequest.getCpf()).isPresent()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "CPF já cadastrado.");
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

        // 1. Cria e salva o User com a role
        User newUser = new User();
        newUser.setNome(registerRequest.getNome());
        newUser.setSobrenome(registerRequest.getSobrenome());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(registerRequest.getPassword()); // A senha deve ser criptografada aqui em um projeto real
        newUser.setCpf(registerRequest.getCpf());
        newUser.setRole(registerRequest.getRole());

        User savedUser = userRepository.save(newUser);

        // 2. Lógica para criar o perfil específico (Funcionario, Tutor, Veterinario)
        if (savedUser.getRole() == UserRole.ROLE_ADMIN || savedUser.getRole() == UserRole.ROLE_FUNCIONARIO) {
            Funcionario newFuncionario = new Funcionario();
            newFuncionario.setId(savedUser.getId());
            newFuncionario.setUser(savedUser);
            newFuncionario.setCargo("Atendente");
            newFuncionario.setAdministrador(savedUser.getRole() == UserRole.ROLE_ADMIN);
            funcionarioRepository.save(newFuncionario);
        } else if (savedUser.getRole() == UserRole.ROLE_VETERINARIO) {
            Veterinario newVeterinario = new Veterinario();
            newVeterinario.setId(savedUser.getId());
            newVeterinario.setUser(savedUser);
            newVeterinario.setCrmv("A definir");
            newVeterinario.setEspecialidade("A definir");
            veterinarioRepository.save(newVeterinario);
        } else if (savedUser.getRole() == UserRole.ROLE_TUTOR) {
            Tutor newTutor = new Tutor();
            newTutor.setId(savedUser.getId());
            newTutor.setUser(savedUser);
            // Estes campos vêm do RegisterRequest
            newTutor.setEndereco(registerRequest.getEndereco());
            newTutor.setTelefone(registerRequest.getTelefone());
            // newTutor.setDataNascimento(registerRequest.getDataNascimento()); // Se tiver no DTO e for obrigatório
            tutorRepository.save(newTutor);
        }


        Map<String, String> response = new HashMap<>();
        response.put("message", "Usuário e perfil registrados com sucesso!");
        response.put("userId", savedUser.getId().toString());
        response.put("email", savedUser.getEmail());
        response.put("role", savedUser.getRole().name());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}