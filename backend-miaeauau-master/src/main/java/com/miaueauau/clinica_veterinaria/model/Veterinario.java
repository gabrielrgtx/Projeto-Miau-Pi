package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
// REMOVER: JsonIdentityInfo, ObjectIdGenerators, JsonIgnoreProperties imports

@Entity
@Table(name = "veterinarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Veterinario {

    @Id
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true) // Manter EAGER
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Column(nullable = false, unique = true)
    private String crmv;

    @Column(nullable = false)
    private String especialidade;

    @OneToMany(mappedBy = "veterinario", fetch = FetchType.LAZY) // Manter LAZY
    private List<Consulta> consultas;

    @OneToMany(mappedBy = "veterinario", fetch = FetchType.LAZY) // Manter LAZY
    private List<DisponibilidadeVeterinario> disponibilidades;
}