package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
// REMOVER: JsonIdentityInfo, ObjectIdGenerators, JsonIgnoreProperties imports

@Entity
@Table(name = "disponibilidades_veterinarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilidadeVeterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER) // MUDANÇA AQUI: EAGER para Veterinario
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    @Column(nullable = false)
    private LocalDateTime inicio;

    @Column(nullable = false)
    private LocalDateTime fim;

    private String observacoes;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DiaSemana diaSemana;
}