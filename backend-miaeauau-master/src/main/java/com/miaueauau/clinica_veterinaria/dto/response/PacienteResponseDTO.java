package com.miaueauau.clinica_veterinaria.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List; // Certifique-se de que List está importado aqui


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteResponseDTO {
    private Long id;
    private String nome;
    private String especie;
    private String raca;
    private LocalDate dataNascimento;
    private Double peso;
    private Long tutorId;
    private String tutorNomeCompleto;

    public static PacienteResponseDTO fromEntity(com.miaueauau.clinica_veterinaria.model.Paciente paciente) {
        String tutorNomeCompleto = null;
        if (paciente.getTutor() != null && paciente.getTutor().getUser() != null) {
            tutorNomeCompleto = paciente.getTutor().getUser().getNome() + " " + paciente.getTutor().getUser().getSobrenome();
        }
        return new PacienteResponseDTO(
                paciente.getId(),
                paciente.getNome(),
                paciente.getEspecie(),
                paciente.getRaca(),
                paciente.getDataNascimento(),
                paciente.getPeso(),
                paciente.getTutor() != null ? paciente.getTutor().getId() : null,
                tutorNomeCompleto
        );
    }

    public static List<PacienteResponseDTO> fromEntityList(List<com.miaueauau.clinica_veterinaria.model.Paciente> pacientes) {
        return pacientes.stream()
                .map(PacienteResponseDTO::fromEntity)
                .collect(java.util.stream.Collectors.toList());
    }
}