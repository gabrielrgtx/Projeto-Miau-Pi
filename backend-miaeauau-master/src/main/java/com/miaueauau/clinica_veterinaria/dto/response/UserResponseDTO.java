package com.miaueauau.clinica_veterinaria.dto.response;

import com.miaueauau.clinica_veterinaria.model.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String nome;
    private String sobrenome;
    private String email;
    private String cpf;
    private UserRole role;

    public static UserResponseDTO fromEntity(com.miaueauau.clinica_veterinaria.model.User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getNome(),
                user.getSobrenome(),
                user.getEmail(),
                user.getCpf(),
                user.getRole()
        );
    }
}