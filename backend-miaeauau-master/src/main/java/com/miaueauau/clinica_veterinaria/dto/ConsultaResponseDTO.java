package com.miaueauau.clinica_veterinaria.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// DTO para representar o Paciente dentro da ConsultaResponseDTO
@Data
@NoArgsConstructor
@AllArgsConstructor
class PacienteConsultaResponseDTO {
    private Long id;
    private String nome;
    // Adicione outros campos do paciente que você quer ver na consulta (ex: especie, raca)
    private String especie;
    private String raca;
}

// DTO para representar o Veterinario dentro da ConsultaResponseDTO
@Data
@NoArgsConstructor
@AllArgsConstructor
class VeterinarioConsultaResponseDTO {
    private Long id;
    private String nome; // Nome do User do Veterinario
    private String sobrenome; // Sobrenome do User do Veterinario
    private String crmv;
    private String especialidade;
}

// DTO para representar o Procedimento dentro da ConsultaResponseDTO
@Data
@NoArgsConstructor
@AllArgsConstructor
class ProcedimentoConsultaResponseDTO {
    private Long id;
    private String nome;
    private Double preco;
}


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaResponseDTO {
    private Long id;
    private PacienteConsultaResponseDTO paciente; // Paciente simplificado
    private VeterinarioConsultaResponseDTO veterinario; // Veterinario simplificado
    private LocalDateTime dataHora;
    private String motivo;
    private String diagnostico;
    private String tratamento;
    private boolean confirmada;
    private String tipoAtendimento;
    private List<ProcedimentoConsultaResponseDTO> procedimentos; // Lista de procedimentos simplificados

    // Métodos de fábrica para converter de Entidade para DTO
    public static ConsultaResponseDTO fromEntity(com.miaueauau.clinica_veterinaria.model.Consulta consulta) {
        PacienteConsultaResponseDTO pacienteDto = null;
        if (consulta.getPaciente() != null) {
            pacienteDto = new PacienteConsultaResponseDTO(
                    consulta.getPaciente().getId(),
                    consulta.getPaciente().getNome(),
                    consulta.getPaciente().getEspecie(),
                    consulta.getPaciente().getRaca()
            );
        }

        VeterinarioConsultaResponseDTO veterinarioDto = null;
        if (consulta.getVeterinario() != null && consulta.getVeterinario().getUser() != null) {
            veterinarioDto = new VeterinarioConsultaResponseDTO(
                    consulta.getVeterinario().getId(),
                    consulta.getVeterinario().getUser().getNome(),
                    consulta.getVeterinario().getUser().getSobrenome(),
                    consulta.getVeterinario().getCrmv(),
                    consulta.getVeterinario().getEspecialidade()
            );
        }

        List<ProcedimentoConsultaResponseDTO> procedimentosDto = null;
        if (consulta.getProcedimentos() != null) {
            procedimentosDto = consulta.getProcedimentos().stream()
                    .map(p -> new ProcedimentoConsultaResponseDTO(p.getId(), p.getNome(), p.getPreco()))
                    .collect(java.util.stream.Collectors.toList());
        }

        return new ConsultaResponseDTO(
                consulta.getId(),
                pacienteDto,
                veterinarioDto,
                consulta.getDataHora(),
                consulta.getMotivo(),
                consulta.getDiagnostico(),
                consulta.getTratamento(),
                consulta.isConfirmada(),
                consulta.getTipoAtendimento(),
                procedimentosDto
        );
    }

    // Método para converter uma lista de entidades para uma lista de DTOs
    public static List<ConsultaResponseDTO> fromEntityList(List<com.miaueauau.clinica_veterinaria.model.Consulta> consultas) {
        return consultas.stream()
                .map(ConsultaResponseDTO::fromEntity)
                .collect(java.util.stream.Collectors.toList());
    }
}