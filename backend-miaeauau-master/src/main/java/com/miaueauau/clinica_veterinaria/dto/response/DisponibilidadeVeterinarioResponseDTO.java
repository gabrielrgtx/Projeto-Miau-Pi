package com.miaueauau.clinica_veterinaria.dto.response;

import com.miaueauau.clinica_veterinaria.model.DiaSemana;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List; // <<< ADICIONE ESTA LINHA NOVAMENTE


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilidadeVeterinarioResponseDTO {
    private Long id;
    private Long veterinarioId;
    private String veterinarioNomeCompleto;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private String observacoes;
    private DiaSemana diaSemana;

    public static DisponibilidadeVeterinarioResponseDTO fromEntity(com.miaueauau.clinica_veterinaria.model.DisponibilidadeVeterinario disp) {
        String vetNome = null;
        if (disp.getVeterinario() != null && disp.getVeterinario().getUser() != null) {
            vetNome = disp.getVeterinario().getUser().getNome() + " " + disp.getVeterinario().getUser().getSobrenome();
        }
        return new DisponibilidadeVeterinarioResponseDTO(
                disp.getId(),
                disp.getVeterinario() != null ? disp.getVeterinario().getId() : null,
                vetNome,
                disp.getInicio(),
                disp.getFim(),
                disp.getObservacoes(),
                disp.getDiaSemana()
        );
    }

    public static List<DisponibilidadeVeterinarioResponseDTO> fromEntityList(List<com.miaueauau.clinica_veterinaria.model.DisponibilidadeVeterinario> disponibilidades) {
        return disponibilidades.stream()
                .map(DisponibilidadeVeterinarioResponseDTO::fromEntity)
                .collect(java.util.stream.Collectors.toList());
    }
}