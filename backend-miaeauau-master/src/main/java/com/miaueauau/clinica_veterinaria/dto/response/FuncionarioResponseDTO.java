package com.miaueauau.clinica_veterinaria.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List; // <<< ADICIONE ESTA LINHA NOVAMENTE


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuncionarioResponseDTO {
    private Long id;
    private String cargo;
    private boolean administrador;
    private UserResponseDTO user; // User aninhado como DTO

    public static FuncionarioResponseDTO fromEntity(com.miaueauau.clinica_veterinaria.model.Funcionario funcionario) {
        UserResponseDTO userDto = null;
        if (funcionario.getUser() != null) {
            userDto = UserResponseDTO.fromEntity(funcionario.getUser());
        }
        return new FuncionarioResponseDTO(
                funcionario.getId(),
                funcionario.getCargo(),
                funcionario.isAdministrador(),
                userDto
        );
    }

    public static List<FuncionarioResponseDTO> fromEntityList(List<com.miaueauau.clinica_veterinaria.model.Funcionario> funcionarios) {
        return funcionarios.stream()
                .map(FuncionarioResponseDTO::fromEntity)
                .collect(java.util.stream.Collectors.toList());
    }
}