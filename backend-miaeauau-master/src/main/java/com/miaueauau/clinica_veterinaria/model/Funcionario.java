package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// REMOVER: JsonIdentityInfo, ObjectIdGenerators, JsonIgnoreProperties imports

@Entity
@Table(name = "funcionarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario {

    @Id
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true) // Manter EAGER
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Column(nullable = false)
    private String cargo;

    private boolean administrador;
}