package com.miaueauau.clinica_veterinaria.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List; // Para listas de DTOs aninhados se fosse o caso

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VeterinarioResponseDTO {
    private Long id;
    private String crmv;
    private String especialidade;
    private UserResponseDTO user; // User aninhado como DTO

    public static VeterinarioResponseDTO fromEntity(com.miaueauau.clinica_veterinaria.model.Veterinario veterinario) {
        UserResponseDTO userDto = null;
        if (veterinario.getUser() != null) {
            userDto = UserResponseDTO.fromEntity(veterinario.getUser());
        }
        return new VeterinarioResponseDTO(
                veterinario.getId(),
                veterinario.getCrmv(),
                veterinario.getEspecialidade(),
                userDto
        );
    }

    public static List<VeterinarioResponseDTO> fromEntityList(List<com.miaueauau.clinica_veterinaria.model.Veterinario> veterinarios) {
        return veterinarios.stream()
                .map(VeterinarioResponseDTO::fromEntity)
                .collect(java.util.stream.Collectors.toList());
    }
}