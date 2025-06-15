// src/main/java/com/miaueauau/clinica_veterinaria/dto/RegisterRequest.java
package com.miaueauau.clinica_veterinaria.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

import com.miaueauau.clinica_veterinaria.model.UserRole;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 255, message = "O nome não pode ter mais de 255 caracteres.")
    private String nome;

    @NotBlank(message = "O sobrenome é obrigatório.")
    @Size(max = 255, message = "O sobrenome não pode ter mais de 255 caracteres.")
    private String sobrenome;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    private String email;

    @NotBlank(message = "O CPF é obrigatório.")
    @Size(min = 11, max = 14, message = "O CPF deve ter entre 11 e 14 caracteres (com pontos e traço se enviados).")
    private String cpf;

    @NotBlank(message = "A senha é obrigatória.")
    private String password;

    @NotNull(message = "A role do usuário é obrigatória.")
    private UserRole role;

    // NOVOS CAMPOS PARA O REGISTRO DE TUTOR
    private String endereco;
    private String telefone;
    // private LocalDate dataNascimento; // Se precisar no registro para Tutor
}