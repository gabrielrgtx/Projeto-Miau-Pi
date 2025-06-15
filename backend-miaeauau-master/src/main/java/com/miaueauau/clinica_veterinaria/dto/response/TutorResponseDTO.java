package com.miaueauau.clinica_veterinaria.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List; // <<< ADICIONE ESTA LINHA NOVAMENTE


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TutorResponseDTO {
    private Long id;
    private String endereco;
    private String telefone;
    private LocalDate dataNascimento;
    private UserResponseDTO user; // User aninhado como DTO

    public static TutorResponseDTO fromEntity(com.miaueauau.clinica_veterinaria.model.Tutor tutor) {
        UserResponseDTO userDto = null;
        if (tutor.getUser() != null) {
            userDto = UserResponseDTO.fromEntity(tutor.getUser());
        }
        return new TutorResponseDTO(
                tutor.getId(),
                tutor.getEndereco(),
                tutor.getTelefone(),
                tutor.getDataNascimento(),
                userDto
        );
    }

    public static List<TutorResponseDTO> fromEntityList(List<com.miaueauau.clinica_veterinaria.model.Tutor> tutores) {
        return tutores.stream()
                .map(TutorResponseDTO::fromEntity)
                .collect(java.util.stream.Collectors.toList());
    }
}