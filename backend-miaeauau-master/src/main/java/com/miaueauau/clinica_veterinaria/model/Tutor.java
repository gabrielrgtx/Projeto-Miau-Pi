package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
// REMOVER: JsonIdentityInfo, ObjectIdGenerators, JsonIgnoreProperties imports

@Entity
@Table(name = "tutores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tutor {

    @Id
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true) // Manter EAGER
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    private LocalDate dataNascimento;

    @Column(nullable = false)
    private String endereco;

    @Column(nullable = false)
    private String telefone;

    @OneToMany(mappedBy = "tutor", fetch = FetchType.LAZY) // Manter LAZY
    private List<Paciente> pacientes = new ArrayList<>();
}