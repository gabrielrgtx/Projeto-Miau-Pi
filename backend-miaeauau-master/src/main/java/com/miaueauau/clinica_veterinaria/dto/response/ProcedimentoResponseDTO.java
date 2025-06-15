package com.miaueauau.clinica_veterinaria.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List; // <<< ADICIONE ESTA LINHA NOVAMENTE

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcedimentoResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private Double preco;

    public static ProcedimentoResponseDTO fromEntity(com.miaueauau.clinica_veterinaria.model.Procedimento procedimento) {
        return new ProcedimentoResponseDTO(
                procedimento.getId(),
                procedimento.getNome(),
                procedimento.getDescricao(),
                procedimento.getPreco()
        );
    }

    public static List<ProcedimentoResponseDTO> fromEntityList(List<com.miaueauau.clinica_veterinaria.model.Procedimento> procedimentos) {
        return procedimentos.stream()
                .map(ProcedimentoResponseDTO::fromEntity)
                .collect(java.util.stream.Collectors.toList());
    }
}