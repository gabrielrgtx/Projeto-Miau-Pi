package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
// REMOVER: JsonIdentityInfo, ObjectIdGenerators, JsonIgnoreProperties imports

@Entity
@Table(name = "procedimentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Procedimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "O nome do procedimento é obrigatório")
    @Size(max = 255, message = "O nome do procedimento não pode ter mais de 255 caracteres")
    private String nome;

    @Size(max = 1000, message = "A descrição do procedimento não pode ter mais de 1000 caracteres")
    private String descricao;

    @NotNull(message = "O preço do procedimento é obrigatório")
    @Positive(message = "O preço do procedimento deve ser um valor positivo")
    private Double preco;

    @ManyToMany(mappedBy = "procedimentos", fetch = FetchType.LAZY) // Manter LAZY
    private List<Consulta> consultas;
}